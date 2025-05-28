package dataaccess;

import com.google.gson.Gson;
import model.UserData;
import service.BadRequestException;

import java.sql.*;



public class SQLUserDAO implements UserDAO{

    SQLUserDAO() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public void createUser(UserData user) {
        var sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseManager.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            // sets values to the placeholders and then updates
            statement.setString(1, user.username());
            statement.setString(2, user.password());
            statement.setString(3, user.email());
            statement.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new BadRequestException("bad request");
        }
    }

    @Override
    public UserData getUser(String username) {
        var sql = "SELECT username, password, email FROM users WHERE username=?";
        try (Connection connection = DatabaseManager.getConnection()){
            try (var ps = connection.prepareStatement(sql)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()){
                    if (rs.next()) {
                        String password = rs.getString("password");
                        String email = rs.getString("email");
                        return new UserData(username, password, email);
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
    public void clear() {
        var sql = "TRUNCATE users";
        try (Connection connection = DatabaseManager.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
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
            throw new ResponseException(500, String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
