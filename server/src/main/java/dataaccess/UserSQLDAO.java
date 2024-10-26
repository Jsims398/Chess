package dataaccess;

import model.UserData;
import java.sql.*;

public class UserSQLDAO {
    private Connection connection;

    public UserSQLDAO(Connection connection){
        this.connection = connection;
    }
    public void addUser(String username, String password, String email) throws SQLException {
        String query = "INSERT INTO user_data (username, password, email) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, email);
            stmt.executeUpdate();
        }
    }

    public UserData getUserByUsername(String username) throws SQLException {
        String query = "SELECT * FROM user_data WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return new UserData(result.getString("username"), result.getString("password"), result.getString("email"));
            }
        }
        return null;
    }

    public boolean userExists(String username) throws SQLException {
        String query = "SELECT 1 FROM user_data WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            ResultSet result = statement.executeQuery();
            return result.next();
        }
    }
}