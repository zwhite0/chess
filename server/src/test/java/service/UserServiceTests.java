package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Test;
import service.requestsandresults.*;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {

    @Test
    public void registerSuccess(){
        UserDAO users = new MemoryUserDAO();
        AuthDAO auths = new MemoryAuthDAO();
        UserService service = new UserService(users, auths, null);

        users.clear();

        RegisterRequest request = new RegisterRequest("Sam","12345","mail@mail.com");
        RegisterResult result = service.register(request);

        assertEquals("Sam", result.username());
        assertNotNull(result.authToken());
        assertNotNull(auths);
    }

    @Test
    public void registerFailure(){
        UserDAO users = new MemoryUserDAO();
        AuthDAO auths = new MemoryAuthDAO();
        UserService service = new UserService(users, auths, null);

        users.clear();

        RegisterRequest request = new RegisterRequest("Sam",null,"mail@mail.com");
        assertThrows (BadRequestException.class, () -> {
            service.register(request);
        });
    }

    @Test
    public void loginSuccess(){
        UserDAO users = new MemoryUserDAO();
        AuthDAO auths = new MemoryAuthDAO();
        UserService service = new UserService(users, auths, null);

        users.clear();
        users.createUser(new UserData("Sam","12345","mail@mail.com"));

        LoginRequest request = new LoginRequest("Sam","12345");
        LoginResult result = service.login(request);

        assertEquals("Sam", result.username());
        assertNotNull(result.authToken());
        assertNotNull(auths);
    }

    @Test
    public void loginFailure(){
        UserDAO users = new MemoryUserDAO();
        AuthDAO auths = new MemoryAuthDAO();
        UserService service = new UserService(users, auths, null);

        users.clear();
        users.createUser(new UserData("Sam","123456","mail@mail.com"));

        LoginRequest request = new LoginRequest("Sam","12345");
        assertThrows (UnauthorizedException.class, () -> {
            service.login(request);
        });
    }

    @Test
    public void logoutSuccess(){
        UserDAO users = new MemoryUserDAO();
        AuthDAO auths = new MemoryAuthDAO();
        UserService service = new UserService(users, auths, null);

        users.clear();
        users.createUser(new UserData("Sam","12345","mail@mail.com"));

        LoginRequest loginRequest = new LoginRequest("Sam","12345");
        LoginResult loginResult = service.login(loginRequest);
        String authToken = loginResult.authToken();
        LogoutRequest request = new LogoutRequest(authToken);
        LogoutResult result = service.logout(request);

        assertNull(auths.getAuth(authToken));
    }

    @Test
    public void logoutFailure(){
        UserDAO users = new MemoryUserDAO();
        AuthDAO auths = new MemoryAuthDAO();
        UserService service = new UserService(users, auths, null);

        users.clear();
        users.createUser(new UserData("Sam","12345","mail@mail.com"));

        LoginRequest loginRequest = new LoginRequest("Sam","12345");
        LoginResult loginResult = service.login(loginRequest);
        String authToken = loginResult.authToken();
        LogoutRequest request = new LogoutRequest("12345");

        assertThrows (UnauthorizedException.class, () -> {
            service.logout(request);
        });
    }

    @Test
    public void clearSuccess(){
        UserDAO users = new MemoryUserDAO();
        AuthDAO auths = new MemoryAuthDAO();
        GameDAO games = new MemoryGameDAO();
        UserService service = new UserService(users, auths, games);

        users.clear();
        users.createUser(new UserData("Sam","12345","mail@mail.com"));
        auths.createAuth(new AuthData("authToken","username"));
        games.createGame(new GameData(1,"white","black","game", new ChessGame()));

        ClearRequest request = new ClearRequest();
        ClearResult result = service.clear(request);

        assertNull(users.getUser("Sam"));
        assertNull(auths.getAuth("authToken"));
        assertNull(games.getGame(1));
    }
}
