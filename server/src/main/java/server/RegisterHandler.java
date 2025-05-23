package server;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import service.requestsandresults.RegisterRequest;
import service.requestsandresults.RegisterResult;
import service.UserService;

public class RegisterHandler {

    UserDAO users;
    AuthDAO auths;


    RegisterHandler(UserDAO users, AuthDAO auths){
        this.users = users;
        this.auths = auths;
    }

    public String registerHandler(String json) {
        UserService newRegister = new UserService(users, auths, null);
        var serializer = new Gson();
        RegisterRequest registerRequest = serializer.fromJson(json,RegisterRequest.class);
        RegisterResult registerResult = newRegister.register(registerRequest);
        return serializer.toJson(registerResult);
    }
}
