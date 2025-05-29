package dataaccess;

import com.google.gson.Gson;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import service.AlreadyTakenException;
import service.BadRequestException;

import java.sql.*;



public class SQLUserDAO implements UserDAO{

    public SQLUserDAO() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        var sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());

        try (Connection connection = DatabaseManager.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            // sets values to the placeholders and then updates
            statement.setString(1, user.username());
            statement.setString(2, hashedPassword);
            statement.setString(3, user.email());
            statement.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException("bad data access");
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        var sql = "SELECT username, password, email FROM users WHERE username=?";
        try (Connection connection = DatabaseManager.getConnection()){
            try (var ps = connection.prepareStatement(sql)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()){
                    if (rs.next()) {
                        String hashedPassword = rs.getString("password");
                        String email = rs.getString("email");
                        return new UserData(username, hashedPassword, email);
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
    public boolean authorizeUser(String username, String password) throws DataAccessException {
        return BCrypt.checkpw(password, getUser(username).password());
    }

    @Override
    public void clear() throws DataAccessException {
        var sql = "TRUNCATE users";
        try (Connection connection = DatabaseManager.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException("bad data access");
        }
    }


        private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  users (
              `username` varchar(256) NOT NULL PRIMARY KEY,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL
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
            throw new DataAccessException("bad data access");
        }
    }
}
