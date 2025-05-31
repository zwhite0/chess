package server;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import service.*;
import server.requestsandresults.CreateGameRequest;
import server.requestsandresults.CreateGameResult;

public class CreateGameHandler {

    AuthDAO auths;
    GameDAO games;

    CreateGameHandler(AuthDAO auths, GameDAO games){
        this.auths = auths;
        this.games = games;
    }

    public String createGameHandler(String header, String json) throws DataAccessException {
        GameService newCreateGame = new GameService(null, auths, games);
        var serializer = new Gson();
        CreateGameRequest createGameRequest = serializer.fromJson(json, CreateGameRequest.class);
        CreateGameResult createGameResult = newCreateGame.createGame(createGameRequest, header);
        return serializer.toJson(createGameResult);
    }
}
