package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class SQLGameTests {

    private static Connection conn;
    private SQLGameDAO gameDAO = new SQLGameDAO();

    public SQLGameTests() throws DataAccessException {
    }

    @BeforeAll
    public static void setup() throws DataAccessException {
        DatabaseManager.createDatabase();
        try {
            conn = DatabaseManager.getConnection();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    public void clearTable(){
        var sql = "TRUNCATE games";
        try (Connection connection = DatabaseManager.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void createGameSuccess() throws DataAccessException, SQLException {
        GameData testGame = new GameData(1,"white","black","myFunGame",new ChessGame());
        gameDAO.createGame(testGame);

        String sql = "SELECT * FROM games WHERE gameID=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1,1);
            ResultSet rs = ps.executeQuery();

            Assertions.assertTrue(rs.next());
            Assertions.assertEquals("white",rs.getString("whiteUsername"));
            Assertions.assertEquals("black",rs.getString("blackUsername"));
            Assertions.assertEquals("myFunGame",rs.getString("gameName"));
        }
    }

    @Test
    public void createGameFailure() throws DataAccessException, SQLException {
        GameData testGame = new GameData(1,"white","black",null,new ChessGame());

        Assertions.assertThrows(DataAccessException.class, () -> {
            gameDAO.createGame(testGame);
        });
    }

    @Test
    public void getGameSuccess() throws DataAccessException {
        GameData testGame = new GameData(1,"white","black","myFunGame",new ChessGame());
        gameDAO.createGame(testGame);
        GameData game = gameDAO.getGame(1);
        Assertions.assertEquals("white", game.whiteUsername());
        Assertions.assertEquals("black", game.blackUsername());
        Assertions.assertEquals("myFunGame", game.gameName());
    }

    @Test
    public void getGameFailure() throws DataAccessException {
        GameData testGame = new GameData(1,"white","black","myFunGame",new ChessGame());
        gameDAO.createGame(testGame);
        GameData game = gameDAO.getGame(2000);
        assertNull(game);
    }

    @Test
    public void listGamesSuccess() throws DataAccessException {
        GameData testGame1 = new GameData(1,"white","black","myFunGame1",new ChessGame());
        gameDAO.createGame(testGame1);
        GameData testGame2 = new GameData(2,"white","black","myFunGame2",new ChessGame());
        gameDAO.createGame(testGame2);
        Collection<GameData> allGames = new ArrayList<>();
        allGames.add(testGame1);
        allGames.add(testGame2);
        assertEquals(allGames,gameDAO.listGames());
    }

    @Test
    public void listGamesFailure() throws DataAccessException {
        Assertions.assertTrue(gameDAO.listGames().isEmpty());
    }

    @Test
    public void updateGameSuccess() throws DataAccessException {
        GameData testGame = new GameData(1,null,"black","myFunGame",new ChessGame());
        gameDAO.createGame(testGame);
        GameData updatedGame = new GameData(1,"white","black","myFunGame",new ChessGame());
        gameDAO.updateGame(updatedGame);
        assertEquals("white",gameDAO.getGame(1).whiteUsername());
    }

    @Test
    public void updateGameFailure() throws DataAccessException {
        GameData testGame = new GameData(1,null,"black","myFunGame",new ChessGame());
        gameDAO.createGame(testGame);
        GameData updatedGame = new GameData(1,"white","black",null , null);
        assertThrows(DataAccessException.class, () -> {
            gameDAO.updateGame(updatedGame);
        });
    }

    @Test
    public void clearSuccess() throws DataAccessException {
        GameData testGame = new GameData(1,null,"black","myFunGame",new ChessGame());
        gameDAO.createGame(testGame);
        gameDAO.clear();

        String sql = "SELECT * FROM games";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            Assertions.assertFalse(rs.next());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}


