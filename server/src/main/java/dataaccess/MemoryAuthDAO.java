package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO{

    HashMap<String, AuthData> auths = new HashMap<>();

    @Override
    public void createAuth(AuthData auth) {
        auths.put(auth.authToken(), auth);
    }

    @Override
    public AuthData getAuth(String authToken) {
        return auths.get(authToken);
    }

    @Override
    public void deleteAuth(AuthData auth) {
        auths.remove(auth.authToken());
    }

    @Override
    public void clear() {
        auths.clear();
    }
}
