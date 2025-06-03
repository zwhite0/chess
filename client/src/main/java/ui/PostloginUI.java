package ui;

import model.GameData;
import sharedserver.ServerFacade;
import sharedserver.exceptions.ResponseException;
import sharedserver.requestsandresults.*;

import java.util.Arrays;

public class PostloginUI {

    ServerFacade server;
    Status status;
    AuthTokenHolder authTokenHolder;

    public PostloginUI(String serverURL, Status status, AuthTokenHolder authTokenHolder){
        server =  new ServerFacade(serverURL);
        this.status = status;
        this.authTokenHolder = authTokenHolder;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "logout" -> logout();
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String logout() throws ResponseException {
        LogoutRequest logoutRequest = new LogoutRequest(authTokenHolder.authToken);
        server.logout(logoutRequest);
        authTokenHolder.authToken = " ";
        status.status = "LOGGED_OUT";
        return "You've logged out.Type help for a list of possible commands.\n";
    }

    public String createGame(String... params) throws ResponseException {
        if (params.length >= 1) {
            CreateGameRequest createGameRequest = new CreateGameRequest(params[0],authTokenHolder.authToken);
            server.createGame(createGameRequest);
            return String.format("Created new game: %s\n", params[0]);
        }
        throw new ResponseException(400, "Expected: <NAME>");
    }

    public String listGames() throws ResponseException {
        ListGamesRequest listGamesRequest = new ListGamesRequest(authTokenHolder.authToken);
        ListGamesResult listGamesResult = server.listGames(listGamesRequest);
        StringBuilder sb = new StringBuilder();
        sb.append("Games: \n");
        int i = 1;
        String whiteUsername;
        String blackUsername;
        for (GameData gameData : listGamesResult.games()){
            whiteUsername = gameData.whiteUsername();
            blackUsername = gameData.blackUsername();
            if (whiteUsername == null){
                whiteUsername = "empty";
            }
            if (blackUsername == null){
                blackUsername = "empty";
            }
            sb.append(String.format("%d.  Game name: %s     White: %s     Black: %s\n", i, gameData.gameName(), whiteUsername, blackUsername));
        }
        return sb.toString();
    }

    public String help() {
        return """
                Possible commands:
                create <NAME> - a game
                list - games
                join <ID> [WHITE|BLACK] - a game
                observe <ID> - a game
                logout - when you are done
                quit - playing chess
                help - with possible commands
                """;
    }
}
