package org.ling.lbl.bh;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.ling.lbl.DataBase;
import org.ling.lbl.LBL;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class BlackHoleHandler {
        private static final double[] SINi = new double[181];
        private static final double[] SINj = new double[360];
        private static final double[] COSi = new double[181];
        private static final double[] COSj = new double[360];
        private static boolean hasCOSIN = false;

        public static void createCOSIN() {
                Thread thread = new Thread(() -> {
                        for (int i = 0; i <= 180; i++) {
                                SINi[i] = Math.sin(Math.toRadians(i - 90));
                                COSi[i] = Math.cos(Math.toRadians(i - 90));
                        }
                        for (int j = 0; j < 360; j++) {
                                SINj[j] = Math.sin(Math.toRadians(j));
                                COSj[j] = Math.cos(Math.toRadians(j));
                        }
                        hasCOSIN = true;
                });
                thread.start();
        }

        public void spawnBlackHole() {
                new BukkitRunnable() {
                        @Override
                        public void run() {
                                try {
                                        // Получаем все имена черных дыр из базы данных
                                        List<String> blackHoleNames = LBL.getInstance().getDataBase().getAllNames();

                                        // Для каждой черной дыры получаем данные и создаем частицы
                                        for (String name : blackHoleNames) {
                                                double radius = LBL.getInstance().getDataBase().getRadius(name);
                                                Particle particle = LBL.getInstance().getDataBase().getParticle(name);
                                                Location location = LBL.getInstance().getDataBase().getLocation(name);
                                                int quality = LBL.getInstance().getDataBase().getQuality(name);

                                                if (radius < 0 || particle == null || location == null) {
                                                        continue;
                                                }

                                                for (int i = 0; i <= 180; i += quality) {
                                                        for (int j = 0; j < 360; j += quality) {
                                                                double x = radius * COSi[i] * COSj[j] + location.getX();
                                                                double y = radius * SINi[i] + (location.getY() + 0.4);
                                                                double z = radius * COSi[i] * SINj[j] + location.getZ();

                                                                location.getWorld().spawnParticle(particle, x, y, z, 0);
                                                        }
                                                }


                                                for (int j = 0; j < 360; j += 2) {
                                                        double x = (radius + 1) * COSj[j] + location.getX();
                                                        double z = (radius + 1) * SINj[j] + location.getZ();
                                                        Objects.requireNonNull(location.getWorld()).spawnParticle(Particle.END_ROD, x, location.getY() + 0.4, z, 0);
                                                }
                                        }
                                } catch (SQLException e) {
                                        e.printStackTrace();
                                }
                        }
                }.runTaskTimer(LBL.getInstance(), 0, 4);
        }

        public void attractEntity() {
                new BukkitRunnable() {
                        @Override
                        public void run() {
                                try {
                                        List<String> blackHoleNames = LBL.getInstance().getDataBase().getAllNames();

                                        for (String name : blackHoleNames) {
                                                Location location = LBL.getInstance().getDataBase().getLocation(name);


                                                for (Entity entity : Objects.requireNonNull(location.getWorld()).getEntities()) {
                                                        if (entity.getLocation().distanceSquared(location) <= 40 * 40) {

                                                                if (entity instanceof Player && entity.getLocation().distanceSquared(location) <= 40 * 40) {
                                                                        Player player = (Player) entity;
                                                                        if (player.getGameMode().equals(GameMode.SPECTATOR))
                                                                                continue;
                                                                        if (player.getGameMode().equals(GameMode.CREATIVE))
                                                                                continue;
                                                                }

                                                                Location loc1 = entity.getLocation();

                                                                Vector vec1 = loc1.toVector();
                                                                Vector vec2 = location.toVector();


                                                                Vector move = vec2.subtract(vec1).multiply(0.1);

                                                                entity.setVelocity(move);
                                                        }
                                                }
                                        }
                                } catch (SQLException e) {
                                        Bukkit.getLogger().severe("SQL parser error");
                                        return;
                                }
                        }

                }.runTaskTimer(LBL.getInstance(), 0, 2);
        }

        public void killEntity() {
                new BukkitRunnable() {
                        @Override
                        public void run() {
                                try {
                                        List<String> blackHoleNames = LBL.getInstance().getDataBase().getAllNames();

                                        for (String name : blackHoleNames) {
                                                Location location = LBL.getInstance().getDataBase().getLocation(name);
                                                double radius = LBL.getInstance().getDataBase().getRadius(name);


                                                List<Entity> livingEntities = Objects.requireNonNull(location.getWorld()).getEntities();

                                                PotionEffect potionEffect = new PotionEffect(PotionEffectType.BLINDNESS, 100, 10, false, false);
                                                PotionEffect potionEffect1 = new PotionEffect(PotionEffectType.WITHER, 100, 100, false, false);
                                                PotionEffect potionEffect2 = new PotionEffect(PotionEffectType.DARKNESS, 100, 10, false, false);
                                                PotionEffect potionEffect3 = new PotionEffect(PotionEffectType.INVISIBILITY, 100, 10, false, false);


                                                for (Entity entity : livingEntities) {
                                                        if (entity instanceof Damageable && entity.getLocation().distanceSquared(location) <= (radius + 1) * (radius + 1)) {

                                                                if (entity instanceof Player) {
                                                                        Player player = (Player) entity;
                                                                        if (player.getGameMode().equals(GameMode.SPECTATOR)) {
                                                                                continue;
                                                                        }

                                                                        if (player.getGameMode().equals(GameMode.CREATIVE)) {
                                                                                continue;
                                                                        }
                                                                }

                                                                LivingEntity livingEntity = (LivingEntity) entity;

                                                                livingEntity.addPotionEffect(potionEffect);
                                                                livingEntity.addPotionEffect(potionEffect1);
                                                                livingEntity.addPotionEffect(potionEffect2);
                                                                livingEntity.addPotionEffect(potionEffect3);
                                                                livingEntity.damage(3);

                                                        } else {
                                                                if (entity.getLocation().distanceSquared(location) <= (radius + 1) * (radius + 1)) {
                                                                        entity.remove();
                                                                }
                                                        }
                                                }
                                        }
                                } catch (SQLException e) {
                                        throw new RuntimeException(e);
                                }
                        }
                }.runTaskTimer(LBL.getInstance(), 0, 7);
        }


        public void transformBlocks() {
                new BukkitRunnable() {
                        @Override
                        public void run() {
                                try {
                                        List<String> blackHoleNames = LBL.getInstance().getDataBase().getAllNames();

                                        for (String name : blackHoleNames) {
                                                Location location = LBL.getInstance().getDataBase().getLocation(name);
                                                double radius = LBL.getInstance().getDataBase().getRadius(name);

                                                Block block = findNearestBlock(location, 40);
                                                if (block != null && (block.getType() != Material.AIR)) {

                                                        FallingBlock fallingBlock = block.getWorld().spawnFallingBlock(block.getLocation(), block.getBlockData());
                                                        fallingBlock.setDropItem(true);
                                                        fallingBlock.setCancelDrop(false);
                                                        fallingBlock.setGravity(false);
                                                        fallingBlock.setHurtEntities(true);
                                                        fallingBlock.setDamagePerBlock(1);
                                                        fallingBlock.setHurtEntities(true);

                                                        block.setType(Material.AIR);

                                                }
                                        }
                                } catch (SQLException e) {
                                        throw new RuntimeException(e);
                                }
                        }
                }.runTaskTimer(LBL.getInstance(), 0, 2);
        }
        private Block findNearestBlock(Location location, double radius) {
                World world = location.getWorld();
                int centerX = location.getBlockX();
                int centerY = location.getBlockY();
                int centerZ = location.getBlockZ();

                Block nearestBlock = null;
                double nearestDistanceSquared = Double.MAX_VALUE;

                for (int dx = centerX - (int) radius; dx <= centerX + radius; dx++) {
                        for (int dy = centerY - (int) radius; dy <= centerY + radius; dy++) {
                                for (int dz = centerZ - (int) radius; dz <= centerZ + radius; dz++) {
                                        double distanceSquared = (dx - centerX) * (dx - centerX) + (dy - centerY) * (dy - centerY) + (dz - centerZ) * (dz - centerZ);
                                        if (distanceSquared <= radius * radius) {
                                                Block block = world.getBlockAt(dx, dy, dz);
                                                if (!(block.getType() == Material.AIR || block.getType() == Material.SHORT_GRASS || block.getType() == Material.TALL_GRASS || block.getType() == Material.WATER || block.getType() == Material.FIRE)) {
                                                        if (distanceSquared < nearestDistanceSquared) {
                                                                nearestBlock = block;
                                                                nearestDistanceSquared = distanceSquared;
                                                        }
                                                }
                                        }
                                }
                        }
                }
                return nearestBlock;
        }


}
