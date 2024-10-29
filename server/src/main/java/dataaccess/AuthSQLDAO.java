package dataaccess;

import model.AuthData;

import java.sql.SQLException;

public class AuthSQLDAO implements AuthDAO{

    public AuthSQLDAO() throws DataAccessException {
        configureDatabase();
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  auths (
              `username` varchar(256) NOT NULL,
              `authToken` varchar(256) NOT NULL,
              PRIMARY KEY (`authToken`),
              INDEX(authToken),
              INDEX(username)
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
            throw new DataAccessException("Unable to configure Auth database");
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        String query = "SELECT * FROM auths WHERE auth_token = ?";
        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement(query)) {
            statement.setString(1, authToken);
            var result = statement.executeQuery();
            if (result.next()) {
                return new AuthData(result.getString("auth_token"), result.getString("user_id")); // Adjust based on your AuthData structure
            }
            return null;
        } catch (SQLException exception) {
            throw new DataAccessException("Error retrieving auth data");
        }
    }

    @Override
    public void addAuth(AuthData authData) throws BadRequestException {
        String query = "INSERT INTO auths (auth_token, user_id) VALUES (?, ?)";
        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement(query)) {
            statement.setString(1, authData.username());
            statement.setString(2, authData.authToken());
            statement.executeUpdate();
        } catch (SQLException | DataAccessException exception) {
            throw new BadRequestException("Error adding auth data");
        }
    }

    @Override
    public void deleteAuth(String authToken) {
        String query = "DELETE FROM auths WHERE auth_token = ?";
        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement(query)) {
            statement.setString(2, authToken);
            statement.executeUpdate();
        }
        catch (SQLException | DataAccessException e) {
            System.out.println("Failed to Delete Auth");
        }
    }

    @Override
    public void clear() {
        String query = "TRUNCATE TABLE auths";
        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement(query)) {
            statement.executeUpdate();
        } catch (SQLException | DataAccessException exception) {
            System.out.println("Failed to clear Auth");
        }
    }
}
