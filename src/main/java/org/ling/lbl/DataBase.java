package org.ling.lbl;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

import java.sql.*;

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
                            "radius TEXT NOT NULL, " +
                            "particle TEXT NOT NULL, " +
                            "x DOUBLE, " +
                            "y DOUBLE, " +
                            "z DOUBLE, " +
                            "world TEXT NOT NULL" +
                            ")");
                }
        }

        public void closeConnection() throws SQLException {
                if (connection != null && !connection.isClosed()) {
                         connection.close();
                }
        }

        public void saveApplication(String name, double blackHoleRadius, Particle particle, double x, double y, double z, World world) throws SQLException {
                String query = "INSERT INTO " + tableName + "(name, radius, particle, x, y, z, world) VALUES (?, ?, ?, ?, ?, ?, ?)";

                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                        preparedStatement.setString(1, name);
                        preparedStatement.setDouble(2, blackHoleRadius);
                        preparedStatement.setString(3, particle.toString());
                        preparedStatement.setDouble(4, x);
                        preparedStatement.setDouble(5, y);
                        preparedStatement.setDouble(6, z);
                        preparedStatement.setString(7, world.getUID().toString());
                        preparedStatement.executeUpdate();
                }
        }

        public boolean isBlackHoleNameTaken(String name) throws SQLException {
                String query = "SELECT COUNT(*) FROM " + tableName + " WHERE name = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                        preparedStatement.setString(1, name);
                        try (ResultSet resultSet = preparedStatement.executeQuery()) {
                                if (resultSet.next()) {
                                        int count = resultSet.getInt(1);
                                        return count > 0;
                                }
                        }
                }
                return false;
        }

    /*private String getApplicationField(String applicationId, String fieldName) throws SQLException {
        String query = "SELECT " + fieldName + " FROM " + tableName + " WHERE applicationId = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, applicationId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() ? resultSet.getString(fieldName) : null;
            }
        }
    }*/

    /*public String getApplicationUserId(String applicationId) throws SQLException {
        return getApplicationField(applicationId, "userId");
    }*/

    /*public boolean applicationExists(String applicationId) throws SQLException {
        String query = "SELECT * FROM " + tableName + " WHERE applicationId = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, applicationId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        }
    }*/

    /*public List<String> getFieldsValue(String applicationId) throws SQLException {
        List<String> fieldValues = new ArrayList<>();
        String query = "SELECT fieldOneValue, fieldTwoValue, fieldThreeValue, fieldFourValue, fieldFiveValue FROM " + tableName + " WHERE applicationId = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, applicationId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    fieldValues.add(resultSet.getString("fieldOneValue"));
                    fieldValues.add(resultSet.getString("fieldTwoValue"));
                    fieldValues.add(resultSet.getString("fieldThreeValue"));
                    fieldValues.add(resultSet.getString("fieldFourValue"));
                    fieldValues.add(resultSet.getString("fieldFiveValue"));
                }
            }
        }
        return fieldValues;
    }

    public boolean isUserInDatabase(String userId) {
        boolean isInDatabase = false;
        try {
            // Подготовка запроса SQL для проверки наличия пользователя по ID
            String sql = "SELECT COUNT(*) FROM " + tableName + " WHERE userId = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, userId);

            // Выполнение запроса и получение результата
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                isInDatabase = count > 0;
            }

            // Закрытие ресурсов
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            // Обработка ошибок
            e.printStackTrace();
        }
        return isInDatabase;
    }*/
}
