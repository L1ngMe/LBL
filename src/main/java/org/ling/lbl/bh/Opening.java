package org.ling.lbl.bh;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.ling.lbl.LBL;
import org.ling.lbl.bh.api.BlackHole;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class Opening {

        // Флаг для управления генерацией частиц
        private final AtomicBoolean continueParticleGeneration = new AtomicBoolean(true);

        // Метод для описания последовательности событий
        public void opening() {
                try {
                        // Получаем список всех черных дыр из базы данных
                        List<String> blackHoleNames = LBL.getInstance().getDataBase().getAllNames();

                        // Для каждой черной дыры выполняем последовательность событий
                        for (String name : blackHoleNames) {
                                Location location = LBL.getInstance().getDataBase().getLocation(name);

                                // 1. Начинается звуковое сопровождение и появляется черная точка из дыма
                                new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                                if (!continueParticleGeneration.get()) {
                                                        cancel(); // Если флаг установлен на false, отменяем задачу
                                                        return;
                                                }
                                                Objects.requireNonNull(location.getWorld()).spawnParticle(Particle.SMOKE_LARGE, location, 10);
                                        }
                                }.runTaskTimer(LBL.getInstance(), 0, 1); // Запускаем задачу, которая генерирует частицы с дымом

                                // 2. Через 30 секунд появляются несколько белых вспышек вокруг точки
                                new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                                continueParticleGeneration.set(false); // Устанавливаем флаг в false, чтобы остановить генерацию частиц
                                                new BukkitRunnable() {
                                                        @Override
                                                        public void run() {
                                                                // 3. Через 5 секунд происходит взрыв силой 40 единиц и появляется дыра
                                                                Objects.requireNonNull(location.getWorld()).spawnParticle(Particle.FLASH, location, 1);
                                                                location.getWorld().createExplosion(location, 50, true);
                                                                location.getWorld().playSound(location, Sound.MUSIC_GAME, 1, 1);

                                                                // Создаем черную дыру и запускаем ее функции
                                                                BlackHoleHandler blackHoleHandler = new BlackHoleHandler();
                                                                blackHoleHandler.spawnBlackHole();
                                                                blackHoleHandler.killEntity();
                                                                blackHoleHandler.attractEntity();
                                                                blackHoleHandler.transformBlocks();
                                                                cancel();
                                                        }
                                                }.runTaskLater(LBL.getInstance(), 100); // Запускаем задачу через 5 секунд после появления вспышек
                                                // Отменяем задачу после взрыва
                                        }
                                }.runTaskLater(LBL.getInstance(), 600); // Запускаем задачу через 30 секунд после начала

                                // 4. Дыра растет в течение 135 секунд и затем исчезает
                                new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                                location.getWorld().spawnParticle(Particle.FLAME, location, 3, 0, 0, 0, 0.1);
                                                Bukkit.getScheduler().cancelTasks(LBL.getInstance());
                                                try {
                                                        LBL.getInstance().getDataBase().deleteBlackHole(name);
                                                } catch (SQLException e) {
                                                        throw new RuntimeException(e);
                                                }
                                        }
                                }.runTaskLater(LBL.getInstance(), 600 + 135 * 20); // Запускаем задачу через 135 секунд после начала
                        }
                } catch (SQLException e) {
                        throw new RuntimeException(e);
                }
        }
        public void start() {
                new BukkitRunnable() {
                        @Override
                        public void run() {
                                try {
                                        List<String> blackHoleNames = LBL.getInstance().getDataBase().getAllNames();

                                        for (String name : blackHoleNames) {
                                                Location location = LBL.getInstance().getDataBase().getLocation(name);
                                                World world = location.getWorld();


                                                // Появление черной точки из дыма
                                                world.spawnParticle(Particle.SMOKE_LARGE, location, 1);
                                        }
                                } catch (SQLException e) {
                                        throw new RuntimeException(e);
                                }

                                // Запускаем вспышки через 30 секунд
                                new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                                try {
                                                        List<String> blackHoleNames = LBL.getInstance().getDataBase().getAllNames();

                                                        for (String name : blackHoleNames) {
                                                                Location location = LBL.getInstance().getDataBase().getLocation(name);
                                                                World world = location.getWorld();

                                                                // Создаем вспышки
                                                                for (int i = 0; i < 5; i++) {
                                                                        world.spawnParticle(Particle.FLASH, location.clone().add(Math.random() * 10 - 5, 0, Math.random() * 10 - 5), 10);
                                                                }
                                                        }
                                                } catch (SQLException e) {
                                                        throw new RuntimeException(e);
                                                }

                                                // Запускаем взрыв через 5 секунд
                                                new BukkitRunnable() {
                                                        @Override
                                                        public void run() {
                                                                boom();
                                                        }
                                                }.runTaskLater(LBL.getInstance(), 100);
                                        }
                                }.runTaskLater(LBL.getInstance(), 600);
                        }
                }.runTaskTimer(LBL.getInstance(), 0, 3);
        }
        public void boom() {
                try {
                        List<String> blackHoleNames = LBL.getInstance().getDataBase().getAllNames();

                        for (String name : blackHoleNames) {
                                Location location = LBL.getInstance().getDataBase().getLocation(name);
                                World world = location.getWorld();

                                world.playSound(location, Sound.MUSIC_GAME, 1, 1);

                                BlackHoleHandler blackHoleHandler = new BlackHoleHandler();
                                blackHoleHandler.spawnBlackHole();
                                blackHoleHandler.killEntity();
                                blackHoleHandler.attractEntity();
                                blackHoleHandler.transformBlocks();


                                world.createExplosion(location, 40, false, false);
                        }

                        // Запускаем рост черной дыры
                        new BukkitRunnable() {
                                @Override
                                public void run() {
                                        growBlackHole();
                                }
                        }.runTaskLater(LBL.getInstance(), 1350); // 1350 тиков = 135 секунд
                } catch (SQLException e) {
                        throw new RuntimeException(e);
                }
        }

        public void growBlackHole() {
                try {
                        List<String> blackHoleNames = LBL.getInstance().getDataBase().getAllNames();

                        for (String name : blackHoleNames) {
                                Location location = LBL.getInstance().getDataBase().getLocation(name);

                                LBL.getInstance().getDataBase().updateRadius(name, +0.3);
                        }

                        // Запускаем исчезновение черного чела
                        new BukkitRunnable() {
                                @Override
                                public void run() {
                                        try {
                                                List<String> blackHoleNames = LBL.getInstance().getDataBase().getAllNames();

                                                for (String name : blackHoleNames) {
                                                        LBL.getInstance().getDataBase().deleteBlackHole(name);
                                                }
                                        } catch (SQLException e) {
                                                throw new RuntimeException(e);
                                        }
                                }
                        }.runTaskLater(LBL.getInstance(), 20); // 20 тиков = 1 секунда
                } catch (SQLException e) {
                        throw new RuntimeException(e);
                }
        }
}
