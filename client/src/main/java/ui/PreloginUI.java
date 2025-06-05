package ui;

import sharedserver.ServerFacade;
import sharedserver.exceptions.ResponseException;
import sharedserver.requestsandresults.LoginRequest;
import sharedserver.requestsandresults.LoginResult;
import sharedserver.requestsandresults.RegisterRequest;
import sharedserver.requestsandresults.RegisterResult;
import ui.EscapeSequences;

import java.util.Arrays;

public class PreloginUI {

    ServerFacade server;
    AuthTokenHolder authTokenHolder;
    Status status;

    public PreloginUI(String serverURL, Status status, AuthTokenHolder authTokenHolder){
        this.authTokenHolder = authTokenHolder;
        this.status = status;
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
        if (params.length == 2) {
            LoginRequest loginRequest = new LoginRequest(params[0],params[1]);
            LoginResult loginResult = server.login(loginRequest);
            authTokenHolder.authToken = loginResult.authToken();
            status.status = "LOGGED_IN";
            return String.format(EscapeSequences.RESET_TEXT_COLOR +"You signed in as %s\n", loginResult.username());
        }
        throw new ResponseException(400, EscapeSequences.SET_TEXT_COLOR_RED+ "Expected: <USERNAME> <PASSWORD>\n"
                +EscapeSequences.SET_TEXT_COLOR_GREEN + "[LOGGED OUT]>>> ");
    }

    public String register(String... params) throws ResponseException {
        if (params.length == 3) {
            RegisterRequest registerRequest = new RegisterRequest(params[0],params[1],params[2]);
            RegisterResult registerResult = server.register(registerRequest);
            authTokenHolder.authToken = registerResult.authToken();
            status.status = "LOGGED_IN";
            return String.format(EscapeSequences.RESET_TEXT_COLOR +"You registered a new account as %s\n", registerResult.username());
        }
        throw new ResponseException(400, EscapeSequences.SET_TEXT_COLOR_RED+"Expected: <USERNAME> <PASSWORD> <EMAIL>\n"
                +EscapeSequences.SET_TEXT_COLOR_GREEN + "[LOGGED OUT]>>> ");
    }

    public String help() {
        return
                EscapeSequences.SET_TEXT_COLOR_BLUE + "register <USERNAME> <PASSWORD> <EMAIL> " +
                        EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY +"- to create an account\n" +
                        EscapeSequences.SET_TEXT_COLOR_BLUE +"login <USERNAME> <PASSWORD> "+
                        EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + "- to play chess\n" +
                        EscapeSequences.SET_TEXT_COLOR_BLUE +"quit "+
                        EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + "_ playing chess\n" +
                        EscapeSequences.SET_TEXT_COLOR_BLUE + "help " +
                        EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + "- with possible commands\n"+
                EscapeSequences.SET_TEXT_COLOR_GREEN + "[LOGGED OUT]>>> ";
    }
}
