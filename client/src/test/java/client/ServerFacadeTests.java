package client;

import model.GameData;
import org.junit.jupiter.api.*;
import server.Server;
import sharedserver.exceptions.ResponseException;
import sharedserver.ServerFacade;
import sharedserver.exceptions.UnauthorizedException;
import sharedserver.requestsandresults.*;

import java.util.ArrayList;
import java.util.Collection;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade = new ServerFacade("http://localhost:8080");

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(8080);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void clearTables() throws ResponseException {
        facade.clear(new ClearRequest());
    }

    @Test
    public void registerSuccess() throws ResponseException {
        RegisterRequest registerRequest = new RegisterRequest("Jeffrey","password","Jeffrey@email.com");
        RegisterResult registerResult = facade.register(registerRequest);
        Assertions.assertEquals("Jeffrey",registerResult.username());
    }

    @Test
    public void registerFail() throws ResponseException {
        RegisterRequest registerRequest1 = new RegisterRequest("Jeffrey","password","Jeffrey@email.com");
        facade.register(registerRequest1);
        RegisterRequest registerRequest2 = new RegisterRequest("Jeffrey","alsoPassword","alsoJeffrey@email.com");
        RegisterRequest registerRequest3 = new RegisterRequest(null,"alsoPassword","alsoJeffrey@email.com");
        ResponseException ex = Assertions.assertThrows(ResponseException.class, () -> facade.register(registerRequest2));
        Assertions.assertEquals(403, ex.statusCode());
        ex = Assertions.assertThrows(ResponseException.class, () -> facade.register(registerRequest3));
        Assertions.assertEquals(400, ex.statusCode());
    }

    @Test
    public void loginSuccess() throws ResponseException {
        RegisterRequest registerRequest = new RegisterRequest("Jeffrey","password","Jeffrey@email.com");
        facade.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("Jeffrey","password");
        LoginResult loginResult = facade.login(loginRequest);
        Assertions.assertNotNull(loginResult.authToken());
    }

    @Test
    public void loginFailure() throws ResponseException {
        RegisterRequest registerRequest = new RegisterRequest("Jeffrey","password","Jeffrey@email.com");
        facade.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("Jeffrey","notAPassword");
        ResponseException ex = Assertions.assertThrows(ResponseException.class, () -> facade.login(loginRequest));
        Assertions.assertEquals(401, ex.statusCode());
    }

    @Test
    public void logoutSuccess() throws ResponseException {
        RegisterRequest registerRequest = new RegisterRequest("Jeffrey","password","Jeffrey@email.com");
        facade.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("Jeffrey","password");
        LoginResult loginResult = facade.login(loginRequest);
        LogoutRequest logoutRequest = new LogoutRequest(loginResult.authToken());
        Assertions.assertDoesNotThrow(() -> facade.logout(logoutRequest));
    }

    @Test
    public void logoutFailure() throws ResponseException {
        RegisterRequest registerRequest = new RegisterRequest("Jeffrey", "password", "Jeffrey@email.com");
        facade.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("Jeffrey", "password");
        LoginResult loginResult = facade.login(loginRequest);
        LogoutRequest logoutRequest = new LogoutRequest("notAnAuthToken");
        ResponseException ex = Assertions.assertThrows(ResponseException.class, () -> facade.logout(logoutRequest));
        Assertions.assertEquals(401, ex.statusCode());
    }

    @Test
    public void createGameSuccess() throws ResponseException {
        RegisterRequest registerRequest = new RegisterRequest("Jeffrey", "password", "Jeffrey@email.com");
        facade.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("Jeffrey", "password");
        LoginResult loginResult = facade.login(loginRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest("myFunGame",loginResult.authToken());
        CreateGameResult createGameResult = facade.createGame(createGameRequest);
        Assertions.assertNotNull(createGameResult.gameID());
    }

    @Test
    public void createGameFailure() throws ResponseException {
        RegisterRequest registerRequest = new RegisterRequest("Jeffrey", "password", "Jeffrey@email.com");
        facade.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("Jeffrey", "password");
        LoginResult loginResult = facade.login(loginRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest("myFunGame","notAnAuthToken");
        ResponseException ex = Assertions.assertThrows(ResponseException.class, () -> facade.createGame(createGameRequest));
        Assertions.assertEquals(401, ex.statusCode());
    }

    @Test
    public void joinGameSuccess() throws ResponseException {
        RegisterRequest registerRequest = new RegisterRequest("Jeffrey", "password", "Jeffrey@email.com");
        facade.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("Jeffrey", "password");
        LoginResult loginResult = facade.login(loginRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest("myFunGame",loginResult.authToken());
        CreateGameResult createGameResult = facade.createGame(createGameRequest);
        JoinGameRequest joinGameRequest = new JoinGameRequest(loginResult.authToken(),"WHITE", createGameResult.gameID() );
        Assertions.assertDoesNotThrow(() -> facade.joinGame(joinGameRequest));
    }

    @Test
    public void joinGameFailure() throws ResponseException {
        RegisterRequest registerRequest = new RegisterRequest("Jeffrey", "password", "Jeffrey@email.com");
        facade.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("Jeffrey", "password");
        LoginResult loginResult = facade.login(loginRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest("myFunGame", loginResult.authToken());
        CreateGameResult createGameResult = facade.createGame(createGameRequest);
        JoinGameRequest joinGameRequest = new JoinGameRequest(loginResult.authToken(), "WHITE", createGameResult.gameID());
        facade.joinGame(joinGameRequest);
        ResponseException ex = Assertions.assertThrows(ResponseException.class, () -> facade.joinGame(joinGameRequest));
        Assertions.assertEquals(403, ex.statusCode());
    }

    @Test
    public void listGamesSuccess() throws ResponseException {
        RegisterRequest registerRequest = new RegisterRequest("Jeffrey", "password", "Jeffrey@email.com");
        facade.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("Jeffrey", "password");
        LoginResult loginResult = facade.login(loginRequest);
        CreateGameRequest createGameRequest1 = new CreateGameRequest("myFunGame", loginResult.authToken());
        facade.createGame(createGameRequest1);
        CreateGameRequest createGameRequest2 = new CreateGameRequest("AlsoAFunGame", loginResult.authToken());
        facade.createGame(createGameRequest2);
        ListGamesRequest listGamesRequest = new ListGamesRequest(loginResult.authToken());
        ListGamesResult listGamesResult = facade.listGames(listGamesRequest);
        Assertions.assertNotNull(listGamesResult.games());
    }

    @Test
    public void listGamesFailure() throws ResponseException {
        RegisterRequest registerRequest = new RegisterRequest("Jeffrey", "password", "Jeffrey@email.com");
        facade.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("Jeffrey", "password");
        LoginResult loginResult = facade.login(loginRequest);
        CreateGameRequest createGameRequest1 = new CreateGameRequest("myFunGame", loginResult.authToken());
        facade.createGame(createGameRequest1);
        CreateGameRequest createGameRequest2 = new CreateGameRequest("AlsoAFunGame", loginResult.authToken());
        facade.createGame(createGameRequest2);
        ListGamesRequest listGamesRequest = new ListGamesRequest("notAnAuthToken");
        ResponseException ex = Assertions.assertThrows(ResponseException.class, () -> facade.listGames(listGamesRequest));
        Assertions.assertEquals(401, ex.statusCode());
    }
}
