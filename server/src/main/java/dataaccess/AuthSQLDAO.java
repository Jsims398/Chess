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
              `authToken` varchar(512) NOT NULL,
              PRIMARY KEY (`authToken`),
              INDEX(authToken),
              INDEX(username)
            )
            """
    };
    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var connection = DatabaseManager.getConnection()) {
            for (var querys : createStatements) {
                try (var statement = connection.prepareStatement(querys)) {
                    statement.executeUpdate();
                }
            }
        }
        catch (SQLException exception) {
            throw new DataAccessException("Unable to configure Auth database");
        }
    }
    @Override
    public AuthData getAuth(String authToken) throws DataAccessException{
        String query = "SELECT * FROM auths WHERE authToken=?";
        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement(query)){
            statement.setString(1, authToken);
            var result = statement.executeQuery();
            if (result.next()) {
                return new AuthData(result.getString("username"), result.getString("authToken"));
            }
        }
        catch (SQLException exception) {
            throw new DataAccessException("Error retrieving auth data");
        }
        return null;
    }
    @Override
    public void addAuth(AuthData authData) throws BadRequestException {
        String query = "INSERT INTO auths (username, authToken) VALUES (?,?)";
        try (var connection = DatabaseManager.getConnection()) {
            connection.setAutoCommit(false);
            try (var statement = connection.prepareStatement(query)) {
                statement.setString(1, authData.username());
                statement.setString(2, authData.authToken());
                statement.executeUpdate();
                connection.commit();
            }
            catch (SQLException exception) {
                connection.rollback();
                throw new BadRequestException("Error adding auth token");
            }
        }
        catch (SQLException | DataAccessException exception) {
            throw new BadRequestException("Error getting database connection");
        }
    }
    @Override
    public void deleteAuth(String authToken){
        String query = "DELETE FROM auths WHERE authToken = ?";
        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement(query)){
            statement.setString(1, authToken);
            statement.executeUpdate();
        }
        catch (SQLException | DataAccessException exception) {
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
