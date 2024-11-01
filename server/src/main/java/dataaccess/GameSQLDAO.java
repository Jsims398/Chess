package dataaccess;

import com.google.gson.Gson;
import model.GameData;
import chess.ChessGame;

import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.HashSet;

public class GameSQLDAO implements GameDAO {

    public GameSQLDAO() throws DataAccessException {
        configureDatabase();
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS games (
              `id` int NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(256) DEFAULT NULL,
              `blackUsername` varchar(256) DEFAULT NULL,
              `gameName` varchar(256) NOT NULL,
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`id`),
              INDEX(id)
               )
            """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var connection = DatabaseManager.getConnection()) {
            for (var query : createStatements) {
                try (var statement = connection.prepareStatement(query)) {
                    statement.executeUpdate();
                }
            }
        }
        catch (SQLException exception) {
            throw new DataAccessException("Unable to configure Game database");
        }
    }

    private GameData readgame(ResultSet result) throws SQLException{
        var id = result.getInt("id");
        var whiteUsername = result.getString("whiteUsername");
        var blackUsername = result.getString("blackUsername");
        var gameName = result.getString("gameName");
        var game = new Gson().fromJson(result.getString("json"), ChessGame.class);
        return new GameData(id, whiteUsername, blackUsername,gameName,game);
    }

    @Override
    public HashSet<GameData> listGames(){
        var result = new HashSet<GameData>();
        var query = "SELECT id,whiteUsername,blackUsername,gameName,json FROM games";
        try (var connection = DatabaseManager.getConnection()){
            try (var statement = connection.prepareStatement(query)){
                try (var results = statement.executeQuery()) {
                    while (results.next()){
                        result.add(readgame(results));
                    }
                }
            }
        }
        catch (SQLException | DataAccessException exception) {
            System.out.println("couldnt list games");
        }
        return result;
    }
    @Override
    public void createGame(GameData game){
        String query = "INSERT INTO games (id, whiteUsername, blackUsername, gameName, json) VALUES (?, ?, ?, ?, ?)";
        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement(query)){
            statement.setInt(1, game.gameID());
            statement.setString(2, game.whiteUsername());
            statement.setString(3, game.blackUsername());
            statement.setString(4, game.gameName());
            statement.setString(5, new Gson().toJson(new ChessGame()));
            statement.executeUpdate();
        }
        catch (SQLException| DataAccessException exception){
            System.out.println("couldnt create game");
        }
    }
    @Override
    public GameData getGame(int gameID) throws DataAccessException{
        String query = "SELECT * FROM games WHERE id = ?";

        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement(query)){
            statement.setInt(1, gameID);

            try (var result = statement.executeQuery()) {
                if (result.next()){
                    return readgame(result);
                }
            }
        }
        catch (SQLException exception){
            throw new DataAccessException("Error retrieving game: " + exception.getMessage());
        }
        throw new DataAccessException("Game not found, id: " + gameID);
    }

    @Override
    public boolean gameExists(int gameID){
        String query = "SELECT 1 FROM games WHERE id = ?";
        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement(query)) {
            statement.setInt(1, gameID);

            try (var result = statement.executeQuery()) {
                return result.next();
            }
        } catch (SQLException | DataAccessException exception) {
            return false;
        }
    }
    @Override
    public void updateGame(GameData game) throws DataAccessException {
        String query = "UPDATE games SET whiteUsername=?, blackUsername=?, gameName=?, json=? WHERE id=?";
        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement(query)){
            statement.setString(1, game.whiteUsername());
            statement.setString(2, game.blackUsername());
            statement.setString(3, game.gameName());
            statement.setString(4, new Gson().toJson(game));
            statement.setInt(5, game.gameID());
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new DataAccessException("Game not found");
                }
            }
        catch(SQLException exception) {
            System.out.println("failed to update game");
        }
    }
    @Override
    public void clear() {
        String query = "TRUNCATE TABLE games";
        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement(query)) {
            statement.executeUpdate();
        }
        catch (SQLException | DataAccessException exception) {
            System.out.println("Failed to clear games");
        }
    }
}