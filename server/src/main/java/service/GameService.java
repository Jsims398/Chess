package service;

import dataaccess.*;
import model.*;
import dataaccess.GameDAO;

import java.util.HashSet;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class GameService {

    GameDAO gameDAO;
    AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }
    public HashSet<GameData> listGames(String authToken) throws UnauthorizedException {
        AuthData authData;
        try {
            authData = authDAO.getAuth(authToken);
            if (authData == null) {
                throw new UnauthorizedException();
            }
        } catch (DataAccessException exception) {
            throw new UnauthorizedException();
        }
        return gameDAO.listGames();
    }
    //create games
    public int createGame(String authentication, String name)throws UnauthorizedException, BadRequestException{
        if (authentication == null) {
            throw new UnauthorizedException();
        }
        if(name == null || name.isBlank()){
            throw new BadRequestException("Game name is empty");
        }
        AuthData authData;
        try {
            authData = authDAO.getAuth(authentication);
        }
        catch (DataAccessException exception) {
            throw new UnauthorizedException();
        }
        if(authData == null){
            throw new UnauthorizedException();
        }
        int gameID;
        do {
            gameID = ThreadLocalRandom.current().nextInt(1, 100);
        }
        while (gameDAO.gameExists(gameID));
        try {
            gameDAO.createGame(new GameData(gameID, null, null, name, null, GameData.Status.PLAYING));
        }
        catch (DataAccessException exception){
            throw new BadRequestException("failed to create game");
        }
        return gameID;
    }

    //join games
    public boolean joinGame(String authentication, int gameID, String color) throws UnauthorizedException, BadRequestException, DataAccessException {
        AuthData authData = authDAO.getAuth(authentication);
        if (authData == null) {
            throw new UnauthorizedException();
        }
        GameData gameData;
        try {
            gameData = gameDAO.getGame(gameID);
        }
        catch (DataAccessException exception) {
            throw new BadRequestException(exception.getMessage());
        }
        String whitePlayer = gameData.whiteUsername();
        String blackPlayer = gameData.blackUsername();

        if (color == null || !color.equals("WHITE") && !color.equals("BLACK")) {
            throw new BadRequestException("Invalid or missing team color: " + color);
        }
        if (color.equals("WHITE")) {
            if (whitePlayer != null){ return false;}
            else {whitePlayer = authData.username();}
        }
        else {
            if (blackPlayer != null) {
                return false;
            } else {
                blackPlayer = authData.username();
            }
        }
        try {
            gameDAO.updateGame(new GameData(gameID, whitePlayer, blackPlayer, gameData.gameName(), gameData.game(), GameData.Status.PLAYING));
            return true;
        }
        catch(DataAccessException exception){
            throw new BadRequestException("Failed to update/find game");
        }
    }
    //clear
    public void clear(){
        gameDAO.clear();
        authDAO.clear();
    }

    public GameData getGame(Integer gameID) throws DataAccessException {
        GameData game = gameDAO.getGame(gameID);
        if (game == null){
            throw new DataAccessException("Could not find game");
        }
        return game;
    }

    public void leave(String user, Integer gameID) throws DataAccessException {
        if (gameDAO.getGame(gameID) == null){
            throw new DataAccessException("Error: bad request");
        }
        GameData origanal = gameDAO.getGame(gameID);
        GameData updated;

        if (Objects.equals(user, origanal.blackUsername())) {
            updated = new GameData(gameID, origanal.whiteUsername(), null, origanal.gameName(), origanal.game(), GameData.Status.PLAYING);
        } else if (Objects.equals(user, origanal.whiteUsername())) {
            updated = new GameData(gameID, null, origanal.blackUsername(), origanal.gameName(), origanal.game(), GameData.Status.PLAYING);
        } else {
            return;
        }

        gameDAO.updateGame(updated);
    }

    public void updateGame(GameData data) throws DataAccessException {
        gameDAO.updateGame(data);
    }
}