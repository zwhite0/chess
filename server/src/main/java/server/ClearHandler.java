package server;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import sharedserver.exceptions.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import service.*;
import sharedserver.requestsandresults.ClearRequest;
import sharedserver.requestsandresults.ClearResult;

public class ClearHandler {

    UserDAO users;
    AuthDAO auths;
    GameDAO games;

    ClearHandler(UserDAO users, AuthDAO auths, GameDAO games){
        this.users = users;
        this.auths = auths;
        this.games = games;
    }

    public String clearHandler(String json) throws DataAccessException {
        UserService clear = new UserService(users, auths, games);
        var serializer = new Gson();
        ClearRequest clearRequest = serializer.fromJson(json,ClearRequest.class);
        ClearResult clearResult = clear.clear(clearRequest);
        return serializer.toJson(clearResult);
    }
}
