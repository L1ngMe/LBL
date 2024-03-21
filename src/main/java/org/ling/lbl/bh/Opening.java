package org.ling.lbl.bh;

import org.bukkit.*;
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
                try {
                        // Получаем все имена черных дыр из базы данных
                        List<String> blackHoleNames = LBL.getInstance().getDataBase().getAllNames();

                        for (String name : blackHoleNames) {
                                double radius = LBL.getInstance().getDataBase().getRadius(name);
                                Particle particle = LBL.getInstance().getDataBase().getParticle(name);
                                Location location = LBL.getInstance().getDataBase().getLocation(name);
                                int quality = LBL.getInstance().getDataBase().getQuality(name);

                                Server server = LBL.getInstance().getServer();
                                LBL plugin = LBL.getInstance();

                                BukkitTask open = server.getScheduler().runTaskTimer(LBL.getInstance(), () -> {
                                        Objects.requireNonNull(location.getWorld()).spawnParticle(particle, location, 7);
                                }, 0, 4);



                                server.getScheduler().runTaskLater(plugin, () -> {
                                        // Остановка выброса частиц
                                        open.cancel();
                                        Objects.requireNonNull(location.getWorld()).spawnParticle(Particle.FLASH, location, 1);

                                        // Взрыв после вспышки спустя 5 сек
                                        server.getScheduler().runTaskLater(plugin, () -> {
                                                location.getWorld().createExplosion(location, 50, true);

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
                                                                plugin.getDataBase().updateRadius(name, 0.01);
                                                        } catch (SQLException e) {
                                                                e.printStackTrace();
                                                        }
                                                }, 0, 10);

                                                server.getScheduler().runTaskLater(plugin, () -> {
                                                        grow.cancel();
                                                        armorStand.remove();
                                                        try {
                                                                plugin.getDataBase().deleteBlackHole(name);
                                                        } catch (SQLException e) {
                                                                e.printStackTrace();
                                                        }
                                                }, 135 * 20);

                                        }, 5 * 20);

                                }, 30 * 20);


                        }
                } catch (SQLException e) {
                        e.printStackTrace();
                }

        }
}
