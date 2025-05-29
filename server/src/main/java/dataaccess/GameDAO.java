package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    void createGame(GameData game) throws DataAccessException;
    GameData getGame(Integer gameID) throws DataAccessException;
    Collection<GameData> listGames();
    void updateGame(GameData game) throws DataAccessException;
    void clear() throws DataAccessException;
}
