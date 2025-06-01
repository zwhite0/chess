package dataaccess;

import model.UserData;
import sharedserver.exceptions.DataAccessException;

public interface UserDAO {
    void createUser(UserData user) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    void clear() throws DataAccessException;
    boolean authorizeUser(String username, String password) throws DataAccessException;
}
