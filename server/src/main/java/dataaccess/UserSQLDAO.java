package dataaccess;


import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.SQLException;

public class UserSQLDAO implements UserDAO {

    public UserSQLDAO() throws DataAccessException {
        configureDatabase();
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  users (
              `name` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              PRIMARY KEY (`name`),
              INDEX(name)
            )
            """
    };
    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        }
        catch (SQLException exception) {
            throw new DataAccessException("Unable to configure User database");
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        final String query = "SELECT * FROM users WHERE name = ?";
        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            try (var result = statement.executeQuery()) {
                if (result.next()) {
                    return new UserData(result.getString("name"),
                            result.getString("password"),
                            result.getString("email"));
                }
                return null;
            }
        }
        catch (SQLException exception) {
            throw new DataAccessException("Error retrieving user");
        }
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        final String query = "INSERT INTO users (name, password, email) VALUES (?, ?, ?)";
        String encryptPass = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement(query)) {
            statement.setString(1, user.username());
            statement.setString(2, encryptPass);
            statement.setString(3, user.email());
            statement.executeUpdate();
        }
        catch (SQLException exception) {
            System.out.print(exception);
            throw new DataAccessException("Error creating user");
        }
    }

    @Override
    public boolean authenticateUser(String username, String password) throws DataAccessException {
        final String query = "SELECT password FROM users WHERE name = ?";
        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement(query)) {
            statement.setString(1, username);

            try (var result = statement.executeQuery()) {
                if (result.next()) {
                    String storedHash = result.getString("password");
                    return BCrypt.checkpw(password, storedHash);
                }
                return false;
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Error authenticating user");
        }
    }

    @Override
    public void clear() throws DataAccessException {
        final String query = "TRUNCATE TABLE users";
        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement(query)) {
            statement.executeUpdate();
        } catch (SQLException | DataAccessException exception) {
            throw new DataAccessException("Error clearing User SQL");
        }
    }

}