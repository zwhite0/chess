package dataaccess;

import model.UserData;
import org.junit.jupiter.api.*;
import service.BadRequestException;
import service.UnauthorizedException;
import service.requestsandresults.ListGamesRequest;

import javax.xml.crypto.Data;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class SQLUserTests {

    private static Connection conn;

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
    public void createUserSuccess() throws DataAccessException, SQLException {
        SQLUserDAO userDAO = new SQLUserDAO();
        UserData testUser = new UserData("Sam","thisisapassword","email@email.com");
        userDAO.createUser(testUser);

        String sql = "SELECT * FROM users WHERE username=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1,"Sam");
            ResultSet rs = ps.executeQuery();

            Assertions.assertTrue(rs.next());
            Assertions.assertEquals("thisisapassword", rs.getString("password"));
            Assertions.assertEquals("email@email.com",rs.getString("email"));
        }
    }

    @Test
    public void createUserFail() throws DataAccessException, SQLException {
        SQLUserDAO userDAO = new SQLUserDAO();
        UserData testUser = new UserData(null,"thisisapassword","email@email.com");

        assertThrows (BadRequestException.class, () -> {
            userDAO.createUser(testUser);
        });
    }
}
