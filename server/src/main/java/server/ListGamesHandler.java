package server;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import service.GameService;
import service.requestsandresults.ListGamesRequest;
import service.requestsandresults.ListGamesResult;

public class ListGamesHandler {

    AuthDAO auths;
    GameDAO games;

    ListGamesHandler(AuthDAO auths, GameDAO games){
        this.auths = auths;
        this.games = games;
    }

    public String listGamesHandler(String json) {
        GameService newListGames = new GameService(null, auths, games);
        var serializer = new Gson();
        ListGamesRequest listGamesRequest = new ListGamesRequest(json);
        ListGamesResult listGamesResult = newListGames.listGames(listGamesRequest);
        return serializer.toJson(listGamesResult);
    }
}
