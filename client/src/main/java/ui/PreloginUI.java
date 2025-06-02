package ui;

import sharedserver.ServerFacade;
import sharedserver.exceptions.ResponseException;
import sharedserver.requestsandresults.LoginRequest;
import sharedserver.requestsandresults.LoginResult;
import sharedserver.requestsandresults.RegisterRequest;
import sharedserver.requestsandresults.RegisterResult;

import java.util.Arrays;

public class PreloginUI {

    ServerFacade server;

    public PreloginUI(String serverURL){
        server =  new ServerFacade(serverURL);
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "login" -> login(params);
                case "quit" -> "quit";
                case "register" -> register(params);
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String login(String... params) throws ResponseException {
        if (params.length >= 1) {
            LoginRequest loginRequest = new LoginRequest(params[0],params[1]);
            LoginResult loginResult = server.login(loginRequest);
            return String.format("You signed in as %s.", loginResult.username());
        }
        throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD>");
    }

    public String register(String... params) throws ResponseException {
        if (params.length >= 1) {
            RegisterRequest registerRequest = new RegisterRequest(params[0],params[1],params[2]);
            RegisterResult registerResult = server.register(registerRequest);
            return String.format("You registered a new account as %s.", registerResult.username());
        }
        throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD> <EMAIL>");
    }

    public String help() {
        return """
                register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                login <USERNAME> <PASSWORD> - to play chess
                quit - playing chess
                help - with possible commands
                """;
    }
}
