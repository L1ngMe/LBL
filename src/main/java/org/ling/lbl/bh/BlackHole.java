package org.ling.lbl.bh;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.ling.lbl.DataBase;
import org.ling.lbl.LBL;

import java.sql.SQLException;

public class BlackHole implements IBlackHole {

        private String name;
        private double radius;
        private Particle particle;
        private double x;
        private double y;
        private double z;
        private World world;

        @Override
        public void setName(String name) throws SQLException {
                DataBase dataBase = LBL.getInstance().getDataBase();
                if (dataBase.isBlackHoleNameTaken(name)) {
                        throw new IllegalStateException("Black hole with name \"" + name + "\" already exists");
                }
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
        }

        private void saveToDatabase() {
                try {
                        DataBase dataBase = LBL.getInstance().getDataBase();
                        dataBase.saveApplication(name, radius, particle, x, y, z, world);
                } catch (SQLException e) {
                        throw new RuntimeException("Failed to save black hole to database", e);
                }
        }


}
