package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    void createGame(GameData game);
    GameData getGame(Integer gameID);
    Collection<GameData> listGames();
    void updateGame(GameData game);
    void clear();
}
