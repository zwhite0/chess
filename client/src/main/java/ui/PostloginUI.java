package ui;

import chess.*;
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
import java.util.List;

public class PostloginUI {

    ServerFacade server;
    String serverURL;
    Status status;
    AuthTokenHolder authTokenHolder;
    WebSocketFacade ws;
    NotificationHandler notificationHandler;
    InGameUI inGameUI;
    int gameID;

    public PostloginUI(String serverURL, Status status, AuthTokenHolder authTokenHolder,
                       WebSocketFacade ws, NotificationHandler notificationHandler, InGameUI inGameUI){
        server =  new ServerFacade(serverURL);
        this.serverURL = serverURL;
        this.status = status;
        this.authTokenHolder = authTokenHolder;
        this.ws = ws;
        this.notificationHandler = notificationHandler;
        this.inGameUI = inGameUI;
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
            inGameUI.chessGame = chessGame;
            return "";
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
                inGameUI.chessGame = chessGame;
                inGameUI.teamColor = "white";
                return "";
            }
            if (params[1].equals("black") && chessGame != null) {
                inGameUI.chessGame = chessGame;
                inGameUI.teamColor = "black";
                return "";
            }

        }
        throw new ResponseException(400, EscapeSequences.SET_TEXT_COLOR_RED+ "Expected: <GAME NUMBER> [WHITE|BLACK]\n"
                +EscapeSequences.SET_TEXT_COLOR_GREEN + "[LOGGED IN]>>> ");
    }

    public static String drawWhiteBoard(ChessGame game, ChessPosition highlight) {
        ChessBoard board = game.getBoard();
        Collection<ChessMove> allMoves = new ArrayList<>();
        int pieceRow = 0;
        int pieceCol = 0;
        if (highlight != null){
            allMoves = game.validMoves(highlight);
            pieceRow = highlight.getRow();
            pieceCol = highlight.getColumn();
        }
        Collection<ChessPosition> allEndLocations = new ArrayList<>();
        for (ChessMove move : allMoves){
            allEndLocations.add(move.getEndPosition());
        }

        ChessPiece[][] squares = board.getSquares();
        StringBuilder sb = new StringBuilder();
        sb.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY+EscapeSequences.RESET_TEXT_COLOR+
                "    a  b  c  d  e  f  g  h    "+EscapeSequences.RESET_BG_COLOR+ "\n" );
        Collection<Integer> locationsInRow = new ArrayList<>();
        int y = 7;
        while (y>=0){
            locationsInRow.clear();
            for (ChessPosition pos : allEndLocations){
                if (pos.getRow() -1 == y ){
                    locationsInRow.add(pos.getColumn());
                }
            }
            sb.append(String.format(EscapeSequences.SET_BG_COLOR_LIGHT_GREY+EscapeSequences.RESET_TEXT_COLOR+" %d ", y+1));
            if (pieceRow == y+1){
                sb.append(drawRowWhiteFirst(squares[y],false,locationsInRow,pieceCol));
            } else {
                sb.append(drawRowWhiteFirst(squares[y], false, locationsInRow, -1));
            }
            sb.append(String.format(EscapeSequences.SET_BG_COLOR_LIGHT_GREY+EscapeSequences.RESET_TEXT_COLOR+
                    " %d "+EscapeSequences.RESET_BG_COLOR+"\n", y+1));
            y--;
            locationsInRow.clear();
            for (ChessPosition pos : allEndLocations){
                if (pos.getRow() -1 == y ){
                    locationsInRow.add(pos.getColumn());
                }
            }
            sb.append(String.format(EscapeSequences.SET_BG_COLOR_LIGHT_GREY+EscapeSequences.RESET_TEXT_COLOR+" %d ", y+1));
            if (pieceRow == y+1){
                sb.append(drawRowBlackFirst(squares[y],false,locationsInRow,pieceCol));
            }else {
                sb.append(drawRowBlackFirst(squares[y], false, locationsInRow,-1));
            }
            sb.append(String.format(EscapeSequences.SET_BG_COLOR_LIGHT_GREY+EscapeSequences.RESET_TEXT_COLOR+
                    " %d "+EscapeSequences.RESET_BG_COLOR+"\n", y+1));
            y--;
        }
        sb.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY+EscapeSequences.RESET_TEXT_COLOR+
                "    a  b  c  d  e  f  g  h    "+EscapeSequences.RESET_BG_COLOR+ "\n" );
        return sb.toString();
    }

    public static String drawBlackBoard(ChessGame game, ChessPosition highlight) {
        ChessBoard board = game.getBoard();
        Collection<ChessMove> allMoves = new ArrayList<>();
        int pieceRow = 0;
        int pieceCol=0;
        if (highlight != null){
            allMoves = game.validMoves(highlight);
            pieceRow = highlight.getRow();
            pieceCol = highlight.getColumn();
        }
        Collection<ChessPosition> allEndLocations = new ArrayList<>();
        for (ChessMove move : allMoves){
            allEndLocations.add(move.getEndPosition());
        }
        ChessPiece[][] squares = board.getSquares();
        StringBuilder sb = new StringBuilder();
        sb.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY+EscapeSequences.RESET_TEXT_COLOR+
                "    h  g  f  e  d  c  b  a    "+EscapeSequences.RESET_BG_COLOR+ "\n" );
        Collection<Integer> locationsInRow = new ArrayList<>();
        int y = 0;
        while (y<=7){
            locationsInRow.clear();
            for (ChessPosition pos : allEndLocations){
                if (pos.getRow() -1 == y ){
                    locationsInRow.add(pos.getColumn());
                }
            }
            sb.append(String.format(EscapeSequences.SET_BG_COLOR_LIGHT_GREY+EscapeSequences.RESET_TEXT_COLOR+" %d ", y+1));
            if (pieceRow == y+1){
                sb.append(drawRowWhiteFirst(squares[y],true,locationsInRow,pieceCol));
            } else {
                sb.append(drawRowWhiteFirst(squares[y], true, locationsInRow, -1));
            }
            sb.append(String.format(EscapeSequences.SET_BG_COLOR_LIGHT_GREY+EscapeSequences.RESET_TEXT_COLOR+
                    " %d "+EscapeSequences.RESET_BG_COLOR+"\n", y+1));
            y++;
            locationsInRow.clear();
            for (ChessPosition pos : allEndLocations){
                if (pos.getRow() -1 == y ){
                    locationsInRow.add(pos.getColumn());
                }
            }
            sb.append(String.format(EscapeSequences.SET_BG_COLOR_LIGHT_GREY+EscapeSequences.RESET_TEXT_COLOR+" %d ", y+1));
            if (pieceRow == y+1){
                sb.append(drawRowBlackFirst(squares[y],true,locationsInRow,pieceCol));
            }else {
                sb.append(drawRowBlackFirst(squares[y], true, locationsInRow,-1));
            }
            sb.append(String.format(EscapeSequences.SET_BG_COLOR_LIGHT_GREY+EscapeSequences.RESET_TEXT_COLOR+
                    " %d "+EscapeSequences.RESET_BG_COLOR+"\n", y+1));
            y++;
        }
        sb.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY+EscapeSequences.RESET_TEXT_COLOR+
                "    h  g  f  e  d  c  b  a    "+EscapeSequences.RESET_BG_COLOR+ "\n" );
        return sb.toString();
    }



    public static String drawRowWhiteFirst(ChessPiece[] row, boolean backwards, Collection<Integer> locationsInRow, int pieceCol){
        StringBuilder sb = new StringBuilder();
        int x = 0;
        if (backwards){
            x = -7;
        }
        int i = 1;
        while (i<5){
            if (locationsInRow.contains(Math.abs(x)+1)){
                sb.append(drawWhiteSquare(row[Math.abs(x)], true, false));
            }else if (pieceCol == Math.abs(x)+1) {
                sb.append(drawWhiteSquare(row[Math.abs(x)], false, true ));
            }else {
                sb.append(drawWhiteSquare(row[Math.abs(x)], false, false));
            }
            x++;
            if (locationsInRow.contains(Math.abs(x)+1)) {
                sb.append(drawBlackSquare(row[Math.abs(x)], true, false));
            } else if(pieceCol == Math.abs(x)+1) {
                sb.append(drawBlackSquare(row[Math.abs(x)], false, true ));
            }else {
                sb.append(drawBlackSquare(row[Math.abs(x)], false, false));
            }
            x++;
            i++;
        }
        sb.append(EscapeSequences.RESET_BG_COLOR);
        return sb.toString();
    }

    public static String drawRowBlackFirst(ChessPiece[] row,boolean backwards,Collection<Integer> locationsInRow, int pieceCol){
        StringBuilder sb = new StringBuilder();
        int x = 0;
        if (backwards){
            x = -7;
        }
        int i = 1;
        while (i<5){
            if (locationsInRow.contains(Math.abs(x)+1)){
                sb.append(drawBlackSquare(row[Math.abs(x)], true, false));
            }else if (pieceCol == Math.abs(x)+1) {
                sb.append(drawBlackSquare(row[Math.abs(x)], false, true ));
            }else {
                sb.append(drawBlackSquare(row[Math.abs(x)], false, false));
            }
            x++;
            if (locationsInRow.contains(Math.abs(x)+1)) {
                sb.append(drawWhiteSquare(row[Math.abs(x)], true, false));
            }else if (pieceCol == Math.abs(x)+1) {
                sb.append(drawWhiteSquare(row[Math.abs(x)], false, true ));
            } else {
                sb.append(drawWhiteSquare(row[Math.abs(x)], false, false));
            }
            x++;
            i++;
        }
        sb.append(EscapeSequences.RESET_BG_COLOR);
        return sb.toString();
    }

    public static String drawWhiteSquare(ChessPiece square, boolean highlighted, boolean yellow){
        String whiteSquare;
        if (square == null) {
            if (!highlighted) {
                whiteSquare = EscapeSequences.SET_BG_COLOR_WHITE + "   ";
            } else {
                whiteSquare = EscapeSequences.SET_BG_COLOR_GREEN + "   ";
            }
        } else {
            if (!highlighted && !yellow) {
                whiteSquare = EscapeSequences.SET_BG_COLOR_WHITE + getPieceSequence(square);
            } else if (yellow){
                whiteSquare = EscapeSequences.SET_BG_COLOR_YELLOW + getPieceSequence(square);
            }else {
                whiteSquare = EscapeSequences.SET_BG_COLOR_GREEN + getPieceSequence(square);
            }
        }
        return whiteSquare;
    }

    public static String drawBlackSquare(ChessPiece square, boolean highlighted, boolean yellow){
        String blackSquare;
        if (square == null) {
            if (!highlighted){
                blackSquare = EscapeSequences.SET_BG_COLOR_BLACK + "   ";
            } else {
                blackSquare = EscapeSequences.SET_BG_COLOR_DARK_GREEN + "   ";
            }
        } else {
            if (!highlighted && !yellow) {
                blackSquare = EscapeSequences.SET_BG_COLOR_BLACK + getPieceSequence(square);
            } else if (yellow){
                blackSquare = EscapeSequences.SET_BG_COLOR_YELLOW + getPieceSequence(square);
            }else {
                blackSquare = EscapeSequences.SET_BG_COLOR_DARK_GREEN + getPieceSequence(square);
            }
        }
        return blackSquare;
    }
    
    public static String getPieceSequence (ChessPiece piece){
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
