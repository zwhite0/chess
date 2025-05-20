package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import java.util.UUID;

public class UserService {

    UserDAO users;
    AuthDAO auths;

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
