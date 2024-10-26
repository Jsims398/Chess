//package dataaccess;
//
//import model.GameData;
//
//import java.util.HashSet;
//
//public class GameDAOMemory implements GameDAO {
//
//    HashSet<GameData> database;
//
//    public GameDAOMemory() {
//        database = HashSet.newHashSet(20);
//    }
//    //list game
//    @Override
//    public HashSet<GameData> listGames() {
//        return database;
//    }
//    //create game
//    @Override
//    public void createGame(GameData game) {
//        database.add(game);
//    }
//    // get game
//    @Override
//    public GameData getGame(int gameID) throws DataAccessException {
//        for (GameData game : database) {
//            if (game.gameID() == gameID) {
//                return game;
//            }
//        }
//        throw new DataAccessException("Game not found, id: " +gameID);
//    }
//    //game exists
//    @Override
//    public boolean gameExists(int gameID) {
//        for (GameData game : database) {
//            if (game.gameID() == gameID) {
//                return true;
//            }
//        }
//        return false;
//    }
//    //update game
//    @Override
//    public void updateGame(GameData game) {
//        try {
//            database.remove(getGame(game.gameID()));
//            database.add(game);
//        } catch (DataAccessException exception) {
//            database.add(game);
//        }
//    }
//    //clear
//    @Override
//    public void clear() {
//        database = HashSet.newHashSet(20);
//    }
//}
