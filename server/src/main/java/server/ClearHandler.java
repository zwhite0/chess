package server;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import service.*;
import service.RequestsAndResults.ClearRequest;
import service.RequestsAndResults.ClearResult;

public class ClearHandler {

    UserDAO users;
    AuthDAO auths;
    GameDAO games;

    ClearHandler(UserDAO users, AuthDAO auths, GameDAO games){
        this.users = users;
        this.auths = auths;
        this.games = games;
    }

    public String clearHandler(String json){
        UserService clear = new UserService(users, auths, games);
        var serializer = new Gson();
        ClearRequest clearRequest = serializer.fromJson(json,ClearRequest.class);
        ClearResult clearResult = clear.clear(clearRequest);
        return serializer.toJson(clearResult);
    }
}
