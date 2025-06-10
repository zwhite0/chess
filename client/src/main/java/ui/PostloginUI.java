package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import model.GameData;
import sharedserver.ServerFacade;
import sharedserver.exceptions.ResponseException;
import sharedserver.requestsandresults.*;
import ui.EscapeSequences;
import ui.websocket.NotificationHandler;
import ui.websocket.WebSocketFacade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class PostloginUI {

    ServerFacade server;
    String serverURL;
    Status status;
    AuthTokenHolder authTokenHolder;
    WebSocketFacade ws;
    NotificationHandler notificationHandler;
    int gameID;

    public PostloginUI(String serverURL, Status status, AuthTokenHolder authTokenHolder,
                       WebSocketFacade ws, NotificationHandler notificationHandler){
        server =  new ServerFacade(serverURL);
        this.serverURL = serverURL;
        this.status = status;
        this.authTokenHolder = authTokenHolder;
        this.ws = ws;
        this.notificationHandler = notificationHandler;
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
                case "join" -> playGame(params);
                case "observe" -> observeGame(params);
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
        return EscapeSequences.RESET_TEXT_COLOR +"You've logged out. Type help for a list of possible commands.\n"+
                EscapeSequences.SET_TEXT_COLOR_GREEN + "[LOGGED OUT]>>> ";
    }

    public String createGame(String... params) throws ResponseException {
        if (params.length == 1) {
            CreateGameRequest createGameRequest = new CreateGameRequest(params[0],authTokenHolder.authToken);
            server.createGame(createGameRequest);
            return String.format(EscapeSequences.RESET_TEXT_COLOR +"Created new game: %s\n" +
                    EscapeSequences.SET_TEXT_COLOR_GREEN + "[LOGGED IN]>>> ", params[0]);
        }
        throw new ResponseException(400, EscapeSequences.SET_TEXT_COLOR_RED+ "Expected: <NAME>\n"
                +EscapeSequences.SET_TEXT_COLOR_GREEN + "[LOGGED IN]>>> ");
    }

    public String listGames() throws ResponseException {
        ListGamesRequest listGamesRequest = new ListGamesRequest(authTokenHolder.authToken);
        ListGamesResult listGamesResult = server.listGames(listGamesRequest);
        StringBuilder sb = new StringBuilder();
        sb.append(EscapeSequences.RESET_TEXT_COLOR +"Games: \n");
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
            i++;
        }
        sb.append(EscapeSequences.SET_TEXT_COLOR_GREEN + "[LOGGED IN]>>> ");
        return sb.toString();
    }

    public String observeGame(String... params) throws ResponseException {
        this.gameID = -1;
        if (params.length == 1){
            ListGamesResult listGamesResult = server.listGames(new ListGamesRequest(authTokenHolder.authToken));
            Integer gameNumber = Integer.parseInt(params[0]);
            Collection<GameData> gameList = listGamesResult.games();
            Integer i = 1;
            ChessGame chessGame = null;
            for (GameData game : gameList){
                if (i.equals(gameNumber)){
                    chessGame = game.game();
                    this.gameID = game.gameID();
                    break;
                }
                i++;
            }
            if (chessGame==null){
                throw new ResponseException(400, EscapeSequences.SET_TEXT_COLOR_RED+ "Error: Game doesn't exist\n"
                        +EscapeSequences.SET_TEXT_COLOR_GREEN + "[CHESS GAME]>>> ");
            }
            ws = new WebSocketFacade(serverURL,notificationHandler);
            ws.connect(authTokenHolder.authToken, this.gameID);
            status.status = "OBSERVING_GAME";
            return drawWhiteBoard(chessGame.getBoard());
        }
        throw new ResponseException(400, EscapeSequences.SET_TEXT_COLOR_RED+ "Expected: <GAME NUMBER>\n"
                +EscapeSequences.SET_TEXT_COLOR_GREEN + "[LOGGED IN]>>> ");
    }

    public String playGame(String... params) throws ResponseException {
        this.gameID = -1;
        try {
            Integer.parseInt(params[0]);
        } catch (NumberFormatException e){
            throw new ResponseException(400, EscapeSequences.SET_TEXT_COLOR_RED+ "Expected: <GAME NUMBER> [WHITE|BLACK]\n"
                    +EscapeSequences.SET_TEXT_COLOR_GREEN + "[LOGGED IN]>>> ");
        }
        if (params.length == 2){
            Integer gameNumber = Integer.parseInt(params[0]);
            ListGamesResult listGamesResult = server.listGames(new ListGamesRequest(authTokenHolder.authToken));
            Collection<GameData> gameList = listGamesResult.games();
            Integer i = 1;
            ChessGame chessGame = null;
            for (GameData game : gameList){
                if (i == gameNumber){
                    this.gameID = game.gameID();
                    chessGame = game.game();
                    break;
                }
                i++;
            }
            JoinGameRequest joinGameRequest = new JoinGameRequest(authTokenHolder.authToken, params[1].toUpperCase(),gameID);
            server.joinGame(joinGameRequest);
            ws = new WebSocketFacade(serverURL,notificationHandler);
            ws.connect(authTokenHolder.authToken, this.gameID);
            status.status = "IN_GAME";
            if (params[1].equals("white") && chessGame != null){
                return drawWhiteBoard(chessGame.getBoard());
            }
            if (params[1].equals("black") && chessGame != null) {
                return drawBlackBoard(chessGame.getBoard());
            }

        }
        throw new ResponseException(400, EscapeSequences.SET_TEXT_COLOR_RED+ "Expected: <GAME NUMBER> [WHITE|BLACK]\n"
                +EscapeSequences.SET_TEXT_COLOR_GREEN + "[LOGGED IN]>>> ");
    }

    public String drawWhiteBoard(ChessBoard board) {
        ChessPiece[][] squares = board.getSquares();
        StringBuilder sb = new StringBuilder();
        sb.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY+EscapeSequences.RESET_TEXT_COLOR+
                "    a  b  c  d  e  f  g  h    "+EscapeSequences.RESET_BG_COLOR+ "\n" );
        int y = 7;
        while (y>=0){
            sb.append(String.format(EscapeSequences.SET_BG_COLOR_LIGHT_GREY+EscapeSequences.RESET_TEXT_COLOR+" %d ", y+1));
            sb.append(drawRowWhiteFirst(squares[y],false));
            sb.append(String.format(EscapeSequences.SET_BG_COLOR_LIGHT_GREY+EscapeSequences.RESET_TEXT_COLOR+
                    " %d "+EscapeSequences.RESET_BG_COLOR+"\n", y+1));
            y--;
            sb.append(String.format(EscapeSequences.SET_BG_COLOR_LIGHT_GREY+EscapeSequences.RESET_TEXT_COLOR+" %d ", y+1));
            sb.append(drawRowBlackFirst(squares[y],false));
            sb.append(String.format(EscapeSequences.SET_BG_COLOR_LIGHT_GREY+EscapeSequences.RESET_TEXT_COLOR+
                    " %d "+EscapeSequences.RESET_BG_COLOR+"\n", y+1));
            y--;
        }
        sb.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY+EscapeSequences.RESET_TEXT_COLOR+
                "    a  b  c  d  e  f  g  h    "+EscapeSequences.RESET_BG_COLOR+ "\n" );
        return sb.toString();
    }

    public String drawBlackBoard(ChessBoard board) {
        ChessPiece[][] squares = board.getSquares();
        StringBuilder sb = new StringBuilder();
        sb.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY+EscapeSequences.RESET_TEXT_COLOR+
                "    h  g  f  e  d  c  b  a    "+EscapeSequences.RESET_BG_COLOR+ "\n" );
        int y = 0;
        while (y<=7){
            sb.append(String.format(EscapeSequences.SET_BG_COLOR_LIGHT_GREY+EscapeSequences.RESET_TEXT_COLOR+" %d ", y+1));
            sb.append(drawRowWhiteFirst(squares[y],true));
            sb.append(String.format(EscapeSequences.SET_BG_COLOR_LIGHT_GREY+EscapeSequences.RESET_TEXT_COLOR+
                    " %d "+EscapeSequences.RESET_BG_COLOR+"\n", y+1));
            y++;
            sb.append(String.format(EscapeSequences.SET_BG_COLOR_LIGHT_GREY+EscapeSequences.RESET_TEXT_COLOR+" %d ", y+1));
            sb.append(drawRowBlackFirst(squares[y],true));
            sb.append(String.format(EscapeSequences.SET_BG_COLOR_LIGHT_GREY+EscapeSequences.RESET_TEXT_COLOR+
                    " %d "+EscapeSequences.RESET_BG_COLOR+"\n", y+1));
            y++;
        }
        sb.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY+EscapeSequences.RESET_TEXT_COLOR+
                "    h  g  f  e  d  c  b  a    "+EscapeSequences.RESET_BG_COLOR+ "\n" );
        return sb.toString();
    }



    public String drawRowWhiteFirst(ChessPiece[] row, boolean backwards){
        StringBuilder sb = new StringBuilder();
        int x = 0;
        if (backwards){
            x = -7;
        }
        int i = 1;
        while (i<5){
            sb.append(drawWhiteSquare(row[Math.abs(x)]));
            x++;
            sb.append(drawBlackSquare(row[Math.abs(x)]));
            x++;
            i++;
        }
        sb.append(EscapeSequences.RESET_BG_COLOR);
        return sb.toString();
    }

    public String drawRowBlackFirst(ChessPiece[] row,boolean backwards){
        StringBuilder sb = new StringBuilder();
        int x = 0;
        if (backwards){
            x = -7;
        }
        int i = 1;
        while (i<5){
            sb.append(drawBlackSquare(row[Math.abs(x)]));
            x++;
            sb.append(drawWhiteSquare(row[Math.abs(x)]));
            x++;
            i++;
        }
        sb.append(EscapeSequences.RESET_BG_COLOR);
        return sb.toString();
    }

    public String drawWhiteSquare(ChessPiece square){
        String whiteSquare;
        if (square == null) {
            whiteSquare = EscapeSequences.SET_BG_COLOR_WHITE + "   ";
        } else {
            
            whiteSquare = EscapeSequences.SET_BG_COLOR_WHITE  +getPieceSequence(square) ;
        }
        return whiteSquare;
    }

    public String drawBlackSquare(ChessPiece square){
        String blackSquare;
        if (square == null) {
            blackSquare = EscapeSequences.SET_BG_COLOR_BLACK + "   ";
        } else {

            blackSquare = EscapeSequences.SET_BG_COLOR_BLACK  +getPieceSequence(square) ;
        }
        return blackSquare;
    }
    
    public String getPieceSequence (ChessPiece piece){
        String pieceSequence = " ";
        if (piece.getTeamColor().equals(ChessGame.TeamColor.WHITE)){

            if (piece.getPieceType().equals(ChessPiece.PieceType.PAWN)){
                pieceSequence = EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.WHITE_PAWN;
            }
            if (piece.getPieceType().equals(ChessPiece.PieceType.ROOK)){
                pieceSequence = EscapeSequences.SET_TEXT_COLOR_BLUE +EscapeSequences.WHITE_ROOK;
            }
            if (piece.getPieceType().equals(ChessPiece.PieceType.KNIGHT)){
                pieceSequence = EscapeSequences.SET_TEXT_COLOR_BLUE +EscapeSequences.WHITE_KNIGHT;
            }
            if (piece.getPieceType().equals(ChessPiece.PieceType.BISHOP)){
                pieceSequence = EscapeSequences.SET_TEXT_COLOR_BLUE +EscapeSequences.WHITE_BISHOP;
            }
            if (piece.getPieceType().equals(ChessPiece.PieceType.QUEEN)){
                pieceSequence = EscapeSequences.SET_TEXT_COLOR_BLUE +EscapeSequences.WHITE_QUEEN;
            }
            if (piece.getPieceType().equals(ChessPiece.PieceType.KING)){
                pieceSequence = EscapeSequences.SET_TEXT_COLOR_BLUE +EscapeSequences.WHITE_KING;
            }
        } else {
            if (piece.getPieceType().equals(ChessPiece.PieceType.PAWN)){
                pieceSequence = EscapeSequences.SET_TEXT_COLOR_RED +EscapeSequences.BLACK_PAWN;
            }
            if (piece.getPieceType().equals(ChessPiece.PieceType.ROOK)){
                pieceSequence = EscapeSequences.SET_TEXT_COLOR_RED + EscapeSequences.BLACK_ROOK;
            }
            if (piece.getPieceType().equals(ChessPiece.PieceType.KNIGHT)){
                pieceSequence = EscapeSequences.SET_TEXT_COLOR_RED + EscapeSequences.BLACK_KNIGHT;
            }
            if (piece.getPieceType().equals(ChessPiece.PieceType.BISHOP)){
                pieceSequence = EscapeSequences.SET_TEXT_COLOR_RED + EscapeSequences.BLACK_BISHOP;
            }
            if (piece.getPieceType().equals(ChessPiece.PieceType.QUEEN)){
                pieceSequence = EscapeSequences.SET_TEXT_COLOR_RED + EscapeSequences.BLACK_QUEEN;
            }
            if (piece.getPieceType().equals(ChessPiece.PieceType.KING)){
                pieceSequence = EscapeSequences.SET_TEXT_COLOR_RED + EscapeSequences.BLACK_KING;
            }
        }
        return pieceSequence;
    }

    public String help() {
        return
                EscapeSequences.RESET_TEXT_COLOR + "Possible commands:\n"+
                EscapeSequences.SET_TEXT_COLOR_BLUE + "create <NAME> " +
                        EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + "- a game\n"+
                        EscapeSequences.SET_TEXT_COLOR_BLUE + "list "+
                        EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + "- games\n"+
                        EscapeSequences.SET_TEXT_COLOR_BLUE + "join <ID> [WHITE|BLACK] "+
                        EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + "- a game\n"+
                        EscapeSequences.SET_TEXT_COLOR_BLUE + "observe <ID> "+
                        EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY +"- a game\n"+
                        EscapeSequences.SET_TEXT_COLOR_BLUE + "logout "+
                        EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + "- when you are done\n"+
                        EscapeSequences.SET_TEXT_COLOR_BLUE +"quit "+
                        EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + "- playing chess\n"+
                        EscapeSequences.SET_TEXT_COLOR_BLUE + "help "+
                        EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + "- with possible commands\n"+
                        EscapeSequences.SET_TEXT_COLOR_GREEN + "[LOGGED IN]>>> "
                ;
    }
}
