package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class MemoryGameDAO implements GameDAO{

    HashMap<Integer, GameData> games = new HashMap<>();

    @Override
    public void createGame(GameData game) {
        games.put(game.gameID(), game);
    }

    @Override
    public GameData getGame(Integer gameID) {
        return games.get(gameID);
    }

    @Override
    public Collection<GameData> listGames() {
        return List.of();
    }

    @Override
    public void updateGame(GameData game) {
        games.put(game.gameID(), game);
    }

    @Override
    public void clear() {
        games.clear();
    }
}
