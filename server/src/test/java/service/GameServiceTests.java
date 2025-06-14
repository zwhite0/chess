package service;

import chess.ChessGame;
import dataaccess.*;
import sharedserver.exceptions.AlreadyTakenException;
import sharedserver.exceptions.DataAccessException;
import sharedserver.exceptions.ResponseException;
import sharedserver.exceptions.UnauthorizedException;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.Test;
import sharedserver.requestsandresults.*;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {

    @Test
    public void createGameSuccess() throws DataAccessException {
        AuthDAO auths = new MemoryAuthDAO();
        GameDAO games = new MemoryGameDAO();

        GameService service = new GameService(null, auths, games);

        auths.createAuth(new AuthData("authToken","username"));
        CreateGameRequest request = new CreateGameRequest("my game","authToken");
        CreateGameResult result = service.createGame(request, "authToken");

        assertNotNull(result.gameID());
    }

    @Test
    public void createGameFailure() throws DataAccessException {
        AuthDAO auths = new MemoryAuthDAO();
        GameDAO games = new MemoryGameDAO();

        GameService service = new GameService(null, auths, games);

        auths.createAuth(new AuthData("authToken","username"));
        CreateGameRequest request = new CreateGameRequest("my game","authToken");
        service.createGame(request, "authToken");

        assertThrows (UnauthorizedException.class, () -> {
            service.createGame(request,"notAuthToken");
        });
    }

    @Test
    public void joinGameSuccess() throws DataAccessException {
        AuthDAO auths = new MemoryAuthDAO();
        GameDAO games = new MemoryGameDAO();

        GameService service = new GameService(null, auths, games);

        auths.createAuth(new AuthData("authToken","Tim"));
        games.createGame(new GameData(1,null,"black","game",new ChessGame()));
        JoinGameRequest request = new JoinGameRequest("authToken","WHITE",1);
        JoinGameResult result = service.joinGame(request, "authToken");

        assertEquals("Tim", games.getGame(1).whiteUsername());
    }

    @Test
    public void joinGameFailure() throws DataAccessException {
        AuthDAO auths = new MemoryAuthDAO();
        GameDAO games = new MemoryGameDAO();

        GameService service = new GameService(null, auths, games);

        auths.createAuth(new AuthData("authToken","Tim"));
        games.createGame(new GameData(1,null,"black","game",new ChessGame()));
        JoinGameRequest request = new JoinGameRequest("authToken","BLACK",1);

        assertThrows (AlreadyTakenException.class, () -> {
            service.joinGame(request,"authToken");
        });
    }

    @Test
    public void listGamesSuccess() throws DataAccessException, ResponseException {
        AuthDAO auths = new MemoryAuthDAO();
        GameDAO games = new MemoryGameDAO();

        GameService service = new GameService(null, auths, games);

        auths.createAuth(new AuthData("authToken","Tim"));
        GameData game1 = new GameData(1,null,"black","game1",new ChessGame());
        GameData game2 = new GameData(2,"white",null,"game2",new ChessGame());
        games.createGame(game1);
        games.createGame(game2);

        Collection<GameData> gameList = new ArrayList<>();
        gameList.add(game1);
        gameList.add(game2);

        ListGamesRequest request = new ListGamesRequest("authToken");
        ListGamesResult result = service.listGames(request);

        assertEquals(gameList, result.games());
    }

    @Test
    public void listGamesFailure() throws DataAccessException {
        AuthDAO auths = new MemoryAuthDAO();
        GameDAO games = new MemoryGameDAO();

        GameService service = new GameService(null, auths, games);

        auths.createAuth(new AuthData("authToken","Tim"));
        GameData game1 = new GameData(1,null,"black","game1",new ChessGame());
        GameData game2 = new GameData(2,"white",null,"game2",new ChessGame());
        games.createGame(game1);
        games.createGame(game2);

        Collection<GameData> gameList = new ArrayList<>();
        gameList.add(game1);
        gameList.add(game2);

        assertThrows (UnauthorizedException.class, () -> {
            service.listGames(new ListGamesRequest("notAuthToken"));
        });

    }
}
