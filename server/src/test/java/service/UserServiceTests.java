package service;

import chess.ChessGame;
import dataaccess.*;
import sharedserver.exceptions.BadRequestException;
import sharedserver.exceptions.DataAccessException;
import sharedserver.exceptions.UnauthorizedException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Test;
import sharedserver.requestsandresults.*;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {

    @Test
    public void registerSuccess() throws DataAccessException {
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
    public void registerFailure() throws DataAccessException {
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
    public void loginSuccess() throws DataAccessException {
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
    public void loginFailure() throws DataAccessException {
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
    public void logoutSuccess() throws DataAccessException {
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
    public void logoutFailure() throws DataAccessException {
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
    public void clearSuccess() throws DataAccessException {
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
