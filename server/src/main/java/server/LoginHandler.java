package server;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import service.*;
import server.requestsandresults.LoginRequest;
import server.requestsandresults.LoginResult;

public class LoginHandler {

    UserDAO users;
    AuthDAO auths;

    LoginHandler(UserDAO users, AuthDAO auths){
        this.users = users;
        this.auths = auths;
    }

    public String loginHandler(String json) throws DataAccessException {
        UserService newLogin = new UserService(users, auths, null);
        var serializer = new Gson();
        LoginRequest loginRequest = serializer.fromJson(json,LoginRequest.class);
        LoginResult loginResult = newLogin.login(loginRequest);
        return serializer.toJson(loginResult);
    }

}
