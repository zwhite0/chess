package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import service.RequestsAndResults.*;

import java.util.UUID;

public class UserService {

    UserDAO users;
    AuthDAO auths;
    GameDAO games;

    public UserService(UserDAO users, AuthDAO auths, GameDAO games){
        this.users = users;
        this.auths = auths;
        this.games = games;
    }


    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public RegisterResult register(RegisterRequest registerRequest) throws AlreadyTakenException, BadRequestException{
        String username = registerRequest.username();
        String password = registerRequest.password();
        String email = registerRequest.email();
        if (username == null || password == null || email == null){
            throw new BadRequestException("bad request");
        }
        UserData user = users.getUser(username);
        if (user != null){
            throw new AlreadyTakenException();
        } else {
        users.createUser(new UserData(username, password, email));
        String newAuth = UUID.randomUUID().toString();
        auths.createAuth(new AuthData(newAuth,username));
        return new RegisterResult(username,newAuth);
        }
    }

    public LoginResult login(LoginRequest loginRequest) {
        String username = loginRequest.username();
        String password = loginRequest.password();
        if (username == null || password == null){
            throw new BadRequestException("bad request");
        }
        UserData user = users.getUser(username);
        if (user == null || ! password.equals(user.password())){
            throw new UnauthorizedException("unauthorized");
        }
        if (user.email() == null){
            throw new BadRequestException("bad request");
        }
        String newAuth = UUID.randomUUID().toString();
        auths.createAuth(new AuthData(newAuth, username));
        return new LoginResult(username,newAuth);
    }

    public LogoutResult logout(LogoutRequest logoutRequest) throws UnauthorizedException {
        String authToken = null;
        if (logoutRequest != null) {
            authToken = logoutRequest.authToken();
        }
        AuthData auth = auths.getAuth(authToken);
        if (auth == null){
            throw new UnauthorizedException("unauthorized");
        }
        auths.deleteAuth(auth);
        return new LogoutResult();
    }

    public ClearResult clear(ClearRequest clearRequest){
        users.clear();
        auths.clear();
        games.clear();
        return new ClearResult();
    }
}
