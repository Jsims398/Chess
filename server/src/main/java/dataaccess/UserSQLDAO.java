package dataaccess;

import model.UserData;

import java.sql.*;

public class UserSQLDAO implements UserDAO {

    public UserSQLDAO() throws DataAccessException {
        configureDatabase();
    }

    private void configureDatabase() throws DataAccessException {
        String query = """
            CREATE TABLE IF NOT EXISTS users (
                `id` int NOT NULL AUTO_INCREMENT,
                `username` varchar(256) NOT NULL,
                `password` varchar(256) NOT NULL,
                `email` varchar(256),
                PRIMARY KEY (`id`)
            )
        """;

        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement(query)) {
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new DataAccessException("Failed to configure database");
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        String query = "SELECT * FROM users WHERE name = ?";
        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement(query)) {

            statement.setString(1, username);

            try (var result = statement.executeQuery()) {
                if (result.next()) {
                    return new UserData(
                            result.getString("name"),
                            result.getString("password"),
                            result.getString("email")
                    );
                }
                else {
                    return null;
                }
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Failed to get user");
        }
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {

        String insertStatement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";

        try (var connection = DatabaseManager.getConnection();
             var preparedStatement = connection.prepareStatement(insertStatement)) {

            preparedStatement.setString(1, user.username());
            preparedStatement.setString(2, user.password());
            preparedStatement.setString(3, user.email());
            preparedStatement.executeUpdate();
        }
        catch (SQLException exception) {
            throw new DataAccessException("Failed to create user");
        }
    }

    @Override
    public boolean authenticateUser(String username, String password) throws DataAccessException {
        String query = "SELECT * FROM users WHERE name = ? AND password = ?";
        try (var connection = DatabaseManager.getConnection();
             var preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            try (var resultSet = preparedStatement.executeQuery()) {
                return resultSet.next(); // If a row is returned, the user is authenticated
            }
        }
        catch (SQLException exception) {
            throw new DataAccessException("Failed to authenticate user");
        }
    }

    @Override
    public void clear() throws DataAccessException {
        String truncateStatement = "TRUNCATE TABLE users";
        try (var connection = DatabaseManager.getConnection();
             var preparedStatement = connection.prepareStatement(truncateStatement)) {

            preparedStatement.executeUpdate();
        }
        catch (SQLException exception) {
            throw new DataAccessException("Failed to clear users");
        }
    }
}
