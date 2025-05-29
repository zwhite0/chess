package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import service.BadRequestException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SQLGameDAO implements GameDAO{

    SQLGameDAO() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public void createGame(GameData game) {
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
            throw new BadRequestException("bad request");
        }
    }

    @Override
    public GameData getGame(Integer gameID) {
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
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<GameData> listGames() {
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
    public void updateGame(GameData game) {
        var json = new Gson().toJson(game.game());
        String sql = "UPDATE games SET game = ? WHERE gameID = ?";
        try (Connection connection = DatabaseManager.getConnection()){
            try (PreparedStatement ps = connection.prepareStatement(sql)){
                ps.setString(1,json);
                ps.setInt(2,game.gameID());
                ps.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clear() {
        var sql = "TRUNCATE games";
        try (Connection connection = DatabaseManager.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  games (
              `gameID` integer NOT NULL PRIMARY KEY,
              `whiteUsername` varchar(256),
              `blackUsername` varchar(256),
              'gameName' varchar(256) NOT NULL,
              'game' JSON NOT NULL
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
        } catch (SQLException | DataAccessException ex) {
            throw new ResponseException(500, String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
