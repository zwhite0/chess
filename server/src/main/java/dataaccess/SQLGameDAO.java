package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import sharedserver.exceptions.DataAccessException;
import sharedserver.exceptions.ResponseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class SQLGameDAO implements GameDAO{

    public SQLGameDAO() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public void createGame(GameData game) throws DataAccessException {
        var sql = "INSERT INTO games (gameID, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)";
        var json = new Gson().toJson(game.game());
        try (Connection connection = DatabaseManager.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            // sets values to the placeholders and then updates
            statement.setInt(1, game.gameID());
            statement.setString(2, game.whiteUsername());
            statement.setString(3,game.blackUsername());
            statement.setString(4,game.gameName());
            statement.setString(5,json);
            statement.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException("bad data access");
        }
    }

    @Override
    public GameData getGame(Integer gameID) throws DataAccessException {
        var sql = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games WHERE gameID=?";
        try (Connection connection = DatabaseManager.getConnection()){
            try (var ps = connection.prepareStatement(sql)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()){
                    if (rs.next()) {
                        String whiteUsername = rs.getString("whiteUsername");
                        String blackUsername = rs.getString("blackUsername");
                        String gameName = rs.getString("gameName");
                        String json = rs.getString("game");
                        ChessGame game = new Gson().fromJson(json, ChessGame.class);
                        return new GameData(gameID,whiteUsername,blackUsername,gameName,game);
                    } else {
                        return null;
                    }
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException("bad data access");
        }
    }

    @Override
    public Collection<GameData> listGames() throws ResponseException {
        Collection<GameData> gameList = new ArrayList<>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String whiteUsername = rs.getString("whiteUsername");
                        String blackUsername = rs.getString("blackUsername");
                        String gameName = rs.getString("gameName");
                        Integer gameID = rs.getInt("gameID");
                        String json = rs.getString("game");
                        ChessGame game = new Gson().fromJson(json, ChessGame.class);
                        gameList.add(new GameData(gameID,whiteUsername,blackUsername,gameName,game));
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return gameList;
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        var json = new Gson().toJson(game.game());
        String sql = "UPDATE games SET game = ?, whiteUsername = ?, blackUsername = ? WHERE gameID = ?";
        try (Connection connection = DatabaseManager.getConnection()){
            try (PreparedStatement ps = connection.prepareStatement(sql)){
                if (game.game() == null || game.gameName() == null) {
                    throw new DataAccessException("Missing required game fields");
                }
                ps.setString(1,json);
                ps.setString(2,game.whiteUsername());
                ps.setString(3, game.blackUsername());
                ps.setInt(4,game.gameID());
                ps.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException("bad data access");
        }
    }

    @Override
    public void clear() throws DataAccessException {
        var sql = "TRUNCATE games";
        try (Connection connection = DatabaseManager.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException("bad data access");
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS games (
                   `gameID` INTEGER NOT NULL PRIMARY KEY,
                   `whiteUsername` VARCHAR(256),
                   `blackUsername` VARCHAR(256),
                   `gameName` VARCHAR(256) NOT NULL,
                   `game` TEXT NOT NULL
                 )
            """
    };

    private void configureDatabase() throws DataAccessException {
        SQLHelper.configureDatabase(createStatements);
    }
}
