package dataaccess;

import com.google.gson.Gson;
import model.GameData;
import chess.ChessGame;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
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

    private GameData readgame(ResultSet res) throws SQLException {
        var ID = res.getInt("id");
        var white = res.getString("whiteUsername");
        var black = res.getString("blackUsername");
        var gameName = res.getString("gameName");
        var game = new Gson().fromJson(res.getString("json"), ChessGame.class);
        return new GameData(ID, white, black, gameName, game);
    }

    @Override
    public HashSet<GameData> listGames(){
        var result = new ArrayList<GameData>();
        var statement = "SELECT id,whiteUsername,blackUsername,gameName,json FROM games";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readgame(rs));
                    }
                }
            }
        } catch (SQLException | DataAccessException e) {
            System.out.println("couldnt list games");
        }
        return null;
    }

    @Override
    public void createGame(GameData game){
        String query = "INSERT INTO games (id, whiteUsername, blackUsername, gameName, json) VALUES (?, ?, ?, ?, ?)";

        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setInt(1, game.gameID());
            preparedStatement.setString(2, game.whiteUsername());
            preparedStatement.setString(3, game.blackUsername());
            preparedStatement.setString(4, game.gameName());
            preparedStatement.setString(5, new Gson().toJson(new ChessGame()));

            preparedStatement.executeUpdate();
        }
        catch (SQLException| DataAccessException e) {
            System.out.println("couldnt create game");
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String query = "SELECT * FROM games WHERE id = ?";

        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setInt(1, gameID);

            try (var rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return readgame(rs);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving game: " + e.getMessage());
        }
        throw new DataAccessException("Game not found, id: " + gameID);
    }

    @Override
    public boolean gameExists(int gameID) {
        String query = "SELECT 1 FROM games WHERE id = ?";

        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setInt(1, gameID);

            try (var rs = preparedStatement.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException | DataAccessException e) {
            return false;
        }
    }

    @Override
    public void updateGame(GameData game) {
        String query = "UPDATE games SET whiteUsername = ?, blackUsername = ?, gameName = ?, json = ? WHERE id = ?";

        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setInt(1, game.gameID());
            preparedStatement.setString(2, game.whiteUsername());
            preparedStatement.setString(3, game.blackUsername());
            preparedStatement.setString(4, game.gameName());
            preparedStatement.setString(5, new Gson().toJson(game));

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                createGame(game);
            }
        }
        catch (SQLException | DataAccessException e) {
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