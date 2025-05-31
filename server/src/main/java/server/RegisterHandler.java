package server;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import sharedserver.requestsandresults.RegisterRequest;
import sharedserver.requestsandresults.RegisterResult;

import service.UserService;

public class RegisterHandler {

    UserDAO users;
    AuthDAO auths;


    RegisterHandler(UserDAO users, AuthDAO auths){
        this.users = users;
        this.auths = auths;
    }

    public String registerHandler(String json) throws DataAccessException {
        UserService newRegister = new UserService(users, auths, null);
        var serializer = new Gson();
        RegisterRequest registerRequest = serializer.fromJson(json,RegisterRequest.class);
        RegisterResult registerResult = newRegister.register(registerRequest);
        return serializer.toJson(registerResult);
    }
}
