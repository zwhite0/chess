package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.GameData;

public class GameService {

    UserDAO users;
    AuthDAO auths;
    GameDAO games;

    public GameService(UserDAO users, AuthDAO auths, GameDAO games){
        this.users = users;
        this.auths = auths;
        this.games = games;
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
}
