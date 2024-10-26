package dataaccess;

import chess.ChessGame;
import com.google.gson.*;
import model.GameData;

import java.sql.*;
import java.util.HashSet;

public class GameSQLDAO implements GameDAO {
    private final Gson gson = new Gson();
    public GameSQLDAO() throws DataAccessException {
        configureDatabase();
    }
    private void configureDatabase() throws DataAccessException {
        try (var connection = DatabaseManager.getConnection()) {
            String createStatement = """
                    CREATE TABLE IF NOT EXISTS games (
                      `id` int NOT NULL AUTO_INCREMENT,
                      `whiteUsername` varchar(256),
                      `blackUsername` varchar(256),
                      `gameName` varchar(256) NOT NULL,
                      `game` longtext NOT NULL,
                      PRIMARY KEY (`id`)
                    )
                    """;
            try (var statement = connection.prepareStatement(createStatement)) {
                statement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Failed to configure database: %s", ex.getMessage()));
        }
    }

    @Override
    public HashSet<GameData> listGames() throws DataAccessException, SQLException {
        HashSet<GameData> games = new HashSet<>(0);
        String query = "SELECT * FROM games";
        try(var connection = DatabaseManager.getConnection();
            var statement = connection.createStatement();
            var result = statement.executeQuery(query)){//get the info from the db

            while (result.next()) {
                String gameJson = result.getString("game");
                ChessGame chessGame;
                try {
                    chessGame = gson.fromJson(gameJson, ChessGame.class); //deserialize it to be a chessgame
                }
                catch (JsonSyntaxException exception) {
                    throw new DataAccessException("Failed to parse game JSON");
                }

                GameData game = new GameData(result.getInt("id"),
                        result.getString("whiteUsername"),
                        result.getString("blackUsername"),
                        result.getString("gameName"),
                        chessGame);
                games.add(game);
            }
        } catch (SQLException exception) {
            throw new DataAccessException(String.format("Failed to list games: %s", exception.getMessage()));
        }
        return games;
    }

    @Override
    public void createGame(GameData game) throws DataAccessException {
        String insertStatement = """
            INSERT INTO games (whiteUsername, blackUsername, gameName, game)
            VALUES (?, ?, ?, ?)
        """;

        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement(insertStatement)) {
            String gameJson = gson.toJson(game.game());//convert to json

            statement.setString(1, game.whiteUsername());
            statement.setString(2, game.blackUsername());
            statement.setString(3, game.gameName());
            statement.setString(4, gameJson); //add it to the db
            statement.executeUpdate();
        }
        catch (SQLException exception) {
            throw new DataAccessException(String.format("Failed to create game: %s", exception.getMessage()));
        }
    }


    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String query = "SELECT * FROM games WHERE id = ?";

        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement(query)) {
            statement.setInt(1, gameID);

            try (var result = statement.executeQuery()) {
                if (result.next()) {
                    String gameJson = result.getString("game");
                    ChessGame chessGame;
                    try {
                        chessGame = gson.fromJson(gameJson, ChessGame.class); //deserialize it
                    }
                    catch (JsonSyntaxException exception) {
                        throw new DataAccessException("Failed to parse game JSON");
                    }
                    return new GameData(result.getInt("id"),
                            result.getString("whiteUsername"),
                            result.getString("blackUsername"),
                            result.getString("gameName"),
                            chessGame); //create the new GameData
                }
                else {
                    throw new DataAccessException("Game not found");
                }
            }
        }
        catch (SQLException ex) {
            throw new DataAccessException(String.format("Failed to get game: %s", ex.getMessage()));
        }
    }


    @Override
    public boolean gameExists(int gameID) throws DataAccessException {
        String query = "SELECT 1 FROM games WHERE id = ?";
        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement(query)) {

            statement.setInt(1, gameID);
            try (var result = statement.executeQuery()) {
                return result.next();//return if there is something
            }
        } catch (SQLException exception) {
            throw new DataAccessException(String.format("Failed to check if game exists: %s", exception.getMessage()));
        }
    }


    @Override
    public void updateGame(GameData game) throws DataAccessException {
        String updateStatement = """
        UPDATE games
        SET whiteUsername = ?, blackUsername = ?, gameName = ?, game = ?
        WHERE id = ?
    """;

        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement(updateStatement)) {
            String gameJson = gson.toJson(game.game());

            statement.setString(1, game.whiteUsername());
            statement.setString(2, game.blackUsername());
            statement.setString(3, game.gameName());
            statement.setString(4, gameJson);
            statement.setInt(5, game.gameID());
            statement.executeUpdate();
        }
        catch (SQLException exception) {
            throw new DataAccessException(String.format("Failed to update game: %s", exception.getMessage()));
        }
    }

    @Override
    public void clear() throws DataAccessException {
        String query = "TRUNCATE TABLE games";

        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement(query)) {

            statement.executeUpdate();
        }
        catch (SQLException ex) {
            throw new DataAccessException(String.format("Failed to truncate games table: %s", ex.getMessage()));
        }
    }

}