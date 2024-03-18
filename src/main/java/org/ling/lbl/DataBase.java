package org.ling.lbl;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataBase {

        private final Connection connection;
        private static final String tableName = "database";

        public static String getTableName() {
                return tableName;
        }

        public DataBase(String path) throws SQLException {
                connection = DriverManager.getConnection("jdbc:sqlite:" + path);
                try (Statement statement = connection.createStatement()) {
                        statement.execute("CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                                "name TEXT PRIMARY KEY, " +
                                "radius REAL NOT NULL, " +
                                "particle TEXT NOT NULL, " +
                                "x REAL, " +
                                "y REAL, " +
                                "z REAL, " +
                                "world TEXT NOT NULL, " +
                                "quality INTEGER" +
                                ")");
                }
        }

        public void closeConnection() throws SQLException {
                if (connection != null && !connection.isClosed()) {
                        connection.close();
                }
        }

        public void saveApplication(String name, double blackHoleRadius, Particle particle, double x, double y, double z, World world, int quality) throws SQLException {
                String query = "INSERT INTO " + tableName + "(name, radius, particle, x, y, z, world, quality) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                        preparedStatement.setString(1, name);
                        preparedStatement.setDouble(2, blackHoleRadius);
                        preparedStatement.setString(3, particle.toString());
                        preparedStatement.setDouble(4, x);
                        preparedStatement.setDouble(5, y);
                        preparedStatement.setDouble(6, z);
                        preparedStatement.setString(7, world.getUID().toString());
                        preparedStatement.setInt(8, quality);
                        preparedStatement.executeUpdate();
                }
        }



        public double getRadius(String name) throws SQLException {
                String query = "SELECT radius FROM " + tableName + " WHERE name = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                        preparedStatement.setString(1, name);
                        try (ResultSet resultSet = preparedStatement.executeQuery()) {
                                if (resultSet.next()) {
                                        return resultSet.getDouble("radius");
                                }
                        }
                }
                return 1.5;
        }

        public String getName(String name) throws SQLException {
                String query = "SELECT name FROM " + tableName + " WHERE name = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                        preparedStatement.setString(1, name);
                        try (ResultSet resultSet = preparedStatement.executeQuery()) {
                                if (resultSet.next()) {
                                        return resultSet.getString("name");
                                }
                        }
                }
                return null;
        }

        public Particle getParticle(String name) throws SQLException {
                String query = "SELECT particle FROM " + tableName + " WHERE name = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                        preparedStatement.setString(1, name);
                        try (ResultSet resultSet = preparedStatement.executeQuery()) {
                                if (resultSet.next()) {
                                        return Particle.valueOf(resultSet.getString("particle"));
                                }
                        }
                }
                return Particle.SQUID_INK;
        }
        public int getQuality(String name) throws SQLException {
                String query = "SELECT quality FROM " + tableName + " WHERE name = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                        preparedStatement.setString(1, name);
                        try (ResultSet resultSet = preparedStatement.executeQuery()) {
                                if (resultSet.next()) {
                                        return resultSet.getInt("quality");
                                }
                        }
                }
                return 8;
        }

        public Location getLocation(String name) throws SQLException {
                String query = "SELECT x, y, z, world FROM " + tableName + " WHERE name = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                        preparedStatement.setString(1, name);
                        try (ResultSet resultSet = preparedStatement.executeQuery()) {
                                if (resultSet.next()) {
                                        double x = resultSet.getDouble("x");
                                        double y = resultSet.getDouble("y");
                                        double z = resultSet.getDouble("z");
                                        World world = org.bukkit.Bukkit.getWorld(java.util.UUID.fromString(resultSet.getString("world")));
                                        if (world != null) {
                                                return new Location(world, x, y, z);
                                        }
                                }
                        }
                }
                return null;
        }

        public List<String> getAllNames() throws SQLException {
                List<String> names = new ArrayList<>();
                String query = "SELECT name FROM " + tableName;
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                        try (ResultSet resultSet = preparedStatement.executeQuery()) {
                                while (resultSet.next()) {
                                        names.add(resultSet.getString("name"));
                                }
                        }
                }
                return names;
        }


        public void deleteBlackHole(String name) throws SQLException {
                String query = "DELETE FROM " + tableName + " WHERE name = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                        preparedStatement.setString(1, name);
                        preparedStatement.executeUpdate();
                }
        }

        public void updateRadius(String name, double newRadius) throws SQLException {
                String query = "UPDATE " + tableName + " SET radius = ? WHERE name = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                        preparedStatement.setDouble(1, newRadius);
                        preparedStatement.setString(2, name);
                        preparedStatement.executeUpdate();
                }
        }
}
