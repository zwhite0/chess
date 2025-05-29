package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import service.BadRequestException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class SQLAuthTests {

    private static Connection conn;
    private SQLAuthDAO authDAO = new SQLAuthDAO();

    public SQLAuthTests() throws DataAccessException {
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
        var sql = "TRUNCATE auths";
        try (Connection connection = DatabaseManager.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void createAuthSuccess() throws DataAccessException, SQLException {
        AuthData testAuth = new AuthData("authToken","username");
        authDAO.createAuth(testAuth);

        String sql = "SELECT * FROM auths WHERE authToken=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1,"authToken");
            ResultSet rs = ps.executeQuery();

            Assertions.assertTrue(rs.next());
            Assertions.assertEquals("username",rs.getString("username"));
        }
    }

    @Test
    public void createAuthFailure(){
        AuthData testAuth = new AuthData(null, "username");

        assertThrows(DataAccessException.class, () -> {
            authDAO.createAuth(testAuth);
        });
    }

    @Test
    public void getAuthSuccess() throws DataAccessException {
        AuthData testAuth = new AuthData("authToken","username");
        authDAO.createAuth(testAuth);
        AuthData auth = authDAO.getAuth("authToken");
        Assertions.assertEquals("username", auth.username());
    }

    @Test
    public void getAuthFailure() throws DataAccessException {
        AuthData testAuth = new AuthData("authToken","username");
        authDAO.createAuth(testAuth);
        AuthData auth = authDAO.getAuth("notAToken");
        assertNull(auth);
    }

    @Test
    public void clearSuccess() throws DataAccessException {
        AuthData testAuth = new AuthData("authToken","username");
        authDAO.createAuth(testAuth);
        authDAO.clear();

        String sql = "SELECT * FROM auths";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            Assertions.assertFalse(rs.next());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void deleteAuthSuccess() throws DataAccessException {
        AuthData testAuth = new AuthData("authToken","username");
        authDAO.createAuth(testAuth);
        authDAO.deleteAuth(testAuth);

        assertNull(authDAO.getAuth(testAuth.authToken()));
    }

    @Test
    public void deleteAuthFailure() throws DataAccessException {
        AuthData testAuth = new AuthData("authToken","username");
        authDAO.createAuth(testAuth);
        authDAO.deleteAuth(new AuthData("notAuthToken","notUsername"));

        assertNotNull(authDAO.getAuth(testAuth.authToken()));
    }
}
