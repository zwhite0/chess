package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO{

    private static final HashMap<String, UserData> users = new HashMap<>();

    @Override
    public void createUser(UserData user) {
        users.put(user.username(),user);
    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }

    @Override
    public void clear() {
        users.clear();
    }
}
