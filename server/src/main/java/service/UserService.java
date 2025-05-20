package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import model.UserData;
import java.util.UUID;

public class UserService {

    MemoryUserDAO users = new MemoryUserDAO();
    MemoryAuthDAO auths = new MemoryAuthDAO();

    public RegisterResult register(RegisterRequest registerRequest) throws AlreadyTakenException{
        String username = registerRequest.username();
        String password = registerRequest.password();
        String email = registerRequest.email();
        UserData user = users.getUser(username);
        if (user != null){
            throw new AlreadyTakenException("Username already taken");
        } else {
        users.createUser(new UserData(username, password, email));
        String newAuth = UUID.randomUUID().toString();
        auths.createAuth(new AuthData(newAuth,username));
        return new RegisterResult(username,newAuth);
        }
    }

    public LoginResult login(LoginRequest loginRequest) {
        return new LoginResult(null,null);
    }

    public void logout(LogoutRequest logoutRequest) {}
}
