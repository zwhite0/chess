package server;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import service.GameService;
import service.requestsandresults.JoinGameRequest;
import service.requestsandresults.JoinGameResult;

public class JoinGameHandler {

    AuthDAO auths;
    GameDAO games;

    JoinGameHandler(AuthDAO auths, GameDAO games){
        this.auths = auths;
        this.games = games;
    }

    public String joinGameHandler(String header, String json) throws DataAccessException {
        GameService newJoinGame = new GameService(null, auths, games);
        var serializer = new Gson();
        JoinGameRequest joinGameRequest = serializer.fromJson(json, JoinGameRequest.class);
        JoinGameResult joinGameResult = newJoinGame.joinGame(joinGameRequest, header);
        return serializer.toJson(joinGameResult);
    }
}
