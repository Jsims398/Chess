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
        try {
            authDAO.getAuth(authToken);
        } catch (DataAccessException exception) {
            throw new UnauthorizedException();
        }
        return gameDAO.listGames();
    }

    //create games
    public int createGame(String authentication, String name)throws UnauthorizedException, BadRequestException{
        if(name == null){
            throw new BadRequestException("Game name is empty");
        }

        try {
            authDAO.getAuth(authentication);
        }
        catch (DataAccessException exception) {
            throw new UnauthorizedException();
        }

        int gameID;
        do {
            gameID = ThreadLocalRandom.current().nextInt(1, 100);
        }
        while (gameDAO.gameExists(gameID));

        gameDAO.createGame(new GameData(gameID, null, null, name, null));

        return gameID;
    }

    //join games
    public boolean joinGame(String authentication, int gameID, String color) throws UnauthorizedException, BadRequestException {
        AuthData authData;
        GameData gameData;
        try {
            authData = authDAO.getAuth(authentication);
        } catch (DataAccessException exception) {
            throw new UnauthorizedException();
        }

        try {
            gameData = gameDAO.getGame(gameID);
        } catch (DataAccessException exception) {
            throw new BadRequestException(exception.getMessage());
        }

        String whitePlayer = gameData.whiteUsername();
        String blackPlayer = gameData.blackUsername();

        if (Objects.equals(color, "WHITE")) {
            if (whitePlayer != null) return false;
            else whitePlayer = authData.username();
        } else if (Objects.equals(color, "BLACK")) {
            if (blackPlayer != null) return false;
            else blackPlayer = authData.username();
        } else if (color != null) throw new BadRequestException("%s is not a valid team color".formatted(color));

        gameDAO.updateGame(new GameData(gameID, whitePlayer, blackPlayer, gameData.gameName(), gameData.game()));
        return true;
    }


    //clear
    public void clear(){
        gameDAO.clear();
    }
}
