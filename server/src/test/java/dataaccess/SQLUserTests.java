package dataaccess;

import model.UserData;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;
import service.AlreadyTakenException;
import service.BadRequestException;
import service.UnauthorizedException;
import service.requestsandresults.ListGamesRequest;

import javax.xml.crypto.Data;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

public class SQLUserTests {

    private static Connection conn;
    private SQLUserDAO userDAO = new SQLUserDAO();

    public SQLUserTests() throws DataAccessException {
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
        var sql = "TRUNCATE users";
        try (Connection connection = DatabaseManager.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void createUserSuccess() throws SQLException, DataAccessException {
        UserData testUser = new UserData("Sam","thisisapassword","email@email.com");
        userDAO.createUser(testUser);

        String sql = "SELECT * FROM users WHERE username=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1,"Sam");
            ResultSet rs = ps.executeQuery();

            Assertions.assertTrue(rs.next());
            Assertions.assertEquals("email@email.com",rs.getString("email"));
        }
    }

    @Test
    public void createUserFailure(){
        UserData testUser = new UserData(null,"thisisapassword","email@email.com");

        assertThrows (DataAccessException.class, () -> {
            userDAO.createUser(testUser);
        });
    }

    @Test
    public void getUserSuccess() throws DataAccessException {
        UserData testUser = new UserData("Sam","thisisapassword","email@email.com");
        userDAO.createUser(testUser);
        UserData user = userDAO.getUser("Sam");
        Assertions.assertEquals("email@email.com", user.email());
        Assertions.assertTrue(BCrypt.checkpw("thisisapassword", user.password()));
    }

    @Test
    public void getUserFailure() throws DataAccessException {
        UserData testUser = new UserData("Sam","thisisapassword","email@email.com");
        userDAO.createUser(testUser);
        UserData user = userDAO.getUser("Bill");
        Assertions.assertNull(user);
    }

    @Test
    public void clearSuccess() throws DataAccessException {
        UserData testUser = new UserData("Sam","thisisapassword","email@email.com");
        userDAO.createUser(testUser);
        userDAO.clear();

        String sql = "SELECT * FROM users";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            Assertions.assertFalse(rs.next());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void authorizeUserSuccess() throws DataAccessException {
        UserData testUser = new UserData("Sam","thisisapassword","email@email.com");
        userDAO.createUser(testUser);
        assertTrue(userDAO.authorizeUser("Sam","thisisapassword"));
    }

    @Test
    public void authorizeUserFailure() throws DataAccessException {
        UserData testUser = new UserData("Sam","thisisapassword","email@email.com");
        userDAO.createUser(testUser);
        assertFalse(userDAO.authorizeUser("Sam","thisisnotapassword"));
    }
}
