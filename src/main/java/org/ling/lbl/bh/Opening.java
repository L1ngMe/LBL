package org.ling.lbl.bh;

import org.bukkit.*;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.ling.lbl.LBL;
import org.ling.lbl.bh.api.BlackHole;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class Opening {

        public void runOpening() {
                Server server = LBL.getInstance().getServer();
                LBL plugin = LBL.getInstance();
                Configuration config = LBL.getInstance().getConfig();


                try {
                        // Получаем все имена черных дыр из базы данных
                        List<String> blackHoleNames = LBL.getInstance().getDataBase().getAllNames();

                        for (String name : blackHoleNames) {
                                Particle particle = LBL.getInstance().getDataBase().getParticle(name);
                                Location location = LBL.getInstance().getDataBase().getLocation(name);



                                location.getWorld().playSound(location,
                                        Sound.valueOf(config.getString("opening.soundSettings.sound")),
                                        SoundCategory.valueOf(config.getString("opening.soundSettings.soundCategory")),
                                        config.getInt("opening.soundSettings.volume"),
                                        config.getInt("opening.soundSettings.pitch"));

                                BukkitTask open = server.getScheduler().runTaskTimer(LBL.getInstance(), () -> {
                                        Objects.requireNonNull(location.getWorld()).spawnParticle(particle, location, config.getInt("opening.startSetting.particleCount"));
                                }, 0, config.getInt("opening.startSetting.coolDown"));



                                server.getScheduler().runTaskLater(plugin, () -> {
                                        // Остановка выброса частиц
                                        open.cancel();
                                        Objects.requireNonNull(location.getWorld()).spawnParticle(Particle.FLASH, location, 1);

                                        // Взрыв после вспышки спустя 5 сек
                                        server.getScheduler().runTaskLater(plugin, () -> {
                                                location.getWorld().createExplosion(location, config.getLong("opening.boomLvl"), true);

                                                BlackHoleHandler blackHoleHandler = new BlackHoleHandler();
                                                blackHoleHandler.transformBlocks();
                                                blackHoleHandler.spawnBlackHole();
                                                blackHoleHandler.killEntity();
                                                blackHoleHandler.attractEntity();

                                                ArmorStand armorStand = plugin.getServer().getWorld(location.getWorld().getUID()).spawn(location, ArmorStand.class);
                                                armorStand.setVisible(false);
                                                armorStand.setGravity(false);
                                                armorStand.addScoreboardTag(name);

                                                BukkitTask grow = server.getScheduler().runTaskTimer(plugin, () -> {
                                                        try {
                                                                plugin.getDataBase().updateRadius(name, config.getLong("opening.growSettings.plus"));
                                                        } catch (SQLException e) {
                                                                e.printStackTrace();
                                                        }
                                                }, 0, config.getLong("opening.growSettings.coolDown"));

                                                server.getScheduler().runTaskLater(plugin, () -> {
                                                        grow.cancel();
                                                        armorStand.remove();
                                                        try {
                                                                plugin.getDataBase().deleteBlackHole(name);
                                                        } catch (SQLException e) {
                                                                e.printStackTrace();
                                                        }
                                                }, config.getLong("opening.runTime"));

                                        }, config.getLong("opening.betweenTime"));

                                }, config.getLong("opening.startSetting.time"));


                        }
                } catch (SQLException e) {
                        e.printStackTrace();
                }

        }
}
