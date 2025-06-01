package dataaccess;

import model.AuthData;
import sharedserver.exceptions.DataAccessException;

public interface AuthDAO {
    void createAuth(AuthData auth) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(AuthData auth) throws DataAccessException;
    void clear() throws DataAccessException;
}
