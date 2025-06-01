package dataaccess;

import model.AuthData;
import sharedserver.exceptions.DataAccessException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SQLAuthDAO implements AuthDAO{

    public SQLAuthDAO() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        var sql = "INSERT INTO auths (authToken, username) VALUES (?, ?)";

        try (Connection connection = DatabaseManager.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            // sets values to the placeholders and then updates
            statement.setString(1, auth.authToken());
            statement.setString(2, auth.username());
            statement.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException("bad data access");
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        var sql = "SELECT authToken, username FROM auths WHERE authToken=?";
        try (Connection connection = DatabaseManager.getConnection()){
            try (var ps = connection.prepareStatement(sql)) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()){
                    if (rs.next()) {
                        String username = rs.getString("username");
                        return new AuthData(authToken,username);
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
    public void deleteAuth(AuthData auth) throws DataAccessException {
        var sql = "DELETE FROM auths WHERE authToken=?";
        try (Connection connection = DatabaseManager.getConnection()) {
            try (var statement = connection.prepareStatement(sql)) {
                statement.setString(1, auth.authToken());
                statement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException("bad data access");
        }
    }

    @Override
    public void clear() throws DataAccessException {
        var sql = "TRUNCATE auths";
        try (Connection connection = DatabaseManager.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException("bad data access");
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  auths (
              `authToken` varchar(256) NOT NULL PRIMARY KEY,
              `username` varchar(256) NOT NULL
            )
            """
    };

    private void configureDatabase() throws DataAccessException {
        SQLHelper.configureDatabase(createStatements);
    }
}
