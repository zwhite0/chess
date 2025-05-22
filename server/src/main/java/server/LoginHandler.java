package server;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import service.*;
import service.requestsandresults.LoginRequest;
import service.requestsandresults.LoginResult;

public class LoginHandler {

    UserDAO users;
    AuthDAO auths;

    LoginHandler(UserDAO users, AuthDAO auths){
        this.users = users;
        this.auths = auths;
    }

    public String loginHandler(String json) {
        UserService newLogin = new UserService(users, auths, null);
        var serializer = new Gson();
        LoginRequest loginRequest = serializer.fromJson(json,LoginRequest.class);
        LoginResult loginResult = newLogin.login(loginRequest);
        return serializer.toJson(loginResult);
    }

}
