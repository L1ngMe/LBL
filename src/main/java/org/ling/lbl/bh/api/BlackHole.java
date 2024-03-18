package org.ling.lbl.bh.api;

import org.bukkit.Particle;
import org.bukkit.World;
import org.ling.lbl.DataBase;
import org.ling.lbl.LBL;

import java.sql.SQLException;

public class BlackHole implements IBlackHole {

        private String name;
        private double radius;
        private int quality;
        private Particle particle;
        private double x;
        private double y;
        private double z;
        private World world;

        @Override
        public void setName(String name) {
                this.name = name;
        }

        @Override
        public void setRadius(double radius) {
                this.radius = radius;
        }

        @Override
        public void setParticle(Particle particle) {
                this.particle = particle;
        }

        @Override
        public void setQuality(int quality) {
                this.quality = quality;
        }


        @Override
        public void setX(double x) {
                this.x = x;
        }

        @Override
        public void setY(double y) {
                this.y = y;
        }

        @Override
        public void setZ(double z) {
                this.z = z;
        }

        @Override
        public void setWorld(World world) {
                this.world = world;
        }

        public void build() {
                validateParameters();
                saveToDatabase();
        }

        private void validateParameters() {
                if (name == null || name.isEmpty()) {
                        throw new IllegalStateException("\"name\" is null or empty");
                }
                if (radius < 0.1) {
                        throw new IllegalStateException("\"radius\" cannot be lower than 0.1");
                }
                if (particle == null) {
                        throw new IllegalStateException("\"particle\" is null");
                }
                if (world == null) {
                        throw new IllegalStateException("\"world\" is null");
                }
                if (quality == 0) {
                        throw new IllegalStateException("\"quality\" cannot be <= 0");
                }
        }

        private void saveToDatabase() {
                try {
                        DataBase dataBase = LBL.getInstance().getDataBase();
                        dataBase.saveApplication(name, radius, particle, x, y, z, world, quality);
                } catch (SQLException e) {
                        throw new RuntimeException("Failed to save black hole to database", e);
                }
        }


}
