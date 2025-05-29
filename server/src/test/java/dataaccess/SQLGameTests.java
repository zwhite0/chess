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


}


