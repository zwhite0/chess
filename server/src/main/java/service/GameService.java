package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.GameData;
import service.requestsandresults.*;

public class GameService {

    UserDAO users;
    AuthDAO auths;
    GameDAO games;

    public GameService(UserDAO users, AuthDAO auths, GameDAO games){
        this.users = users;
        this.auths = auths;
        this.games = games;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest, String authToken) throws UnauthorizedException{
        String gameName = createGameRequest.gameName();
        if (gameName == null){
            throw new BadRequestException("bad request");
        }
        AuthData auth = auths.getAuth(authToken);
        if (auth == null){
            throw new UnauthorizedException("unauthorized");
        }
        Integer x = 1;
        while (true) {
            GameData game = games.getGame(x);
            if (game == null) {
                games.createGame(new GameData(x, null, null, gameName, new ChessGame()));
                return new CreateGameResult(x);
            } else {
                x++;
            }
        }
    }

    public JoinGameResult joinGame(JoinGameRequest joinGameRequest, String authToken)
            throws BadRequestException, UnauthorizedException, AlreadyTakenException{
        String playerColor = joinGameRequest.playerColor();
        Integer gameID = joinGameRequest.gameID();
        if (playerColor == null || gameID == null){
            throw new BadRequestException("bad request");
        }
        AuthData auth = auths.getAuth(authToken);
        if (auth == null){
            throw new UnauthorizedException("unauthorized");
        }
        String username = auth.username();
        GameData game = games.getGame(gameID);
        if (game == null){
            throw new BadRequestException("bad request");
        }
        if (playerColor.equals("WHITE")){
            if (game.whiteUsername() == null){
                GameData updatedGame = new GameData(gameID, username, game.blackUsername(), game.gameName(), game.game());
                games.updateGame(updatedGame);
            } else {
                throw new AlreadyTakenException();
            }
        } else if (playerColor.equals("BLACK")) {
            if (game.blackUsername() == null){
                GameData updatedGame = new GameData(gameID, game.whiteUsername(), username, game.gameName(), game.game());
                games.updateGame(updatedGame);
            } else {
                throw new AlreadyTakenException();
            }
        } else {
            throw new BadRequestException("bad request");
        }
        return new JoinGameResult();
    }

    public ListGamesResult listGames(ListGamesRequest listGamesRequest) throws UnauthorizedException{
        String authToken = listGamesRequest.authToken();
        AuthData auth = auths.getAuth(authToken);
        if (auth == null){
            throw new UnauthorizedException("unauthorized");
        }
        return new ListGamesResult(games.listGames());
    }
}
