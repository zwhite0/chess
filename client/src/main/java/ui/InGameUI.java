package ui;

import chess.ChessMove;
import chess.ChessPosition;
import sharedserver.ServerFacade;
import sharedserver.exceptions.ResponseException;
import ui.websocket.NotificationHandler;
import ui.websocket.WebSocketFacade;

import java.util.Arrays;

public class InGameUI {
    ServerFacade server;
    String serverURL;
    Status status;
    AuthTokenHolder authTokenHolder;
    WebSocketFacade ws;
    NotificationHandler notificationHandler;
    Boolean observing;
    int gameID;

    public InGameUI(String serverURL, Status status, AuthTokenHolder authTokenHolder,
                    WebSocketFacade ws, NotificationHandler notificationHandler){
        server =  new ServerFacade(serverURL);
        this.status = status;
        this.authTokenHolder = authTokenHolder;
        this.ws = ws;
        this.notificationHandler = notificationHandler;
        this.serverURL = serverURL;
    }

    public String eval(String input) throws ResponseException {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            if (!observing) {
                return switch (cmd) {
                    case "leave" -> leave();
                    case "move" -> move(params);
                    default -> help();
                };
            } else {
                return switch (cmd) {
                    case "leave" -> leave();
                    default -> helpObserving();
                };
            }
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String leave() throws ResponseException {
        ws.leave(authTokenHolder.authToken, this.gameID);
        status.status = "LOGGED_IN";
        return EscapeSequences.SET_TEXT_COLOR_GREEN + "[LOGGED IN]>>> ";
    }

    public String move(String... params) throws ResponseException {
        if (params.length == 2){
            int startingRow = Character.getNumericValue(params[0].charAt(1));
            int endingRow = Character.getNumericValue(params[1].charAt(1));
            ChessPosition startingPosition = makeChessPosition(params[0].charAt(0),startingRow);
            ChessPosition endingPosition = makeChessPosition(params[1].charAt(0),endingRow);
            ChessMove move = new ChessMove(startingPosition,endingPosition, null);
            ws.move(authTokenHolder.authToken,this.gameID,move);
            return "done";
        } else {
            throw new ResponseException(400, EscapeSequences.SET_TEXT_COLOR_RED + "Expected: PARAMS<YOUR PIECE'S SQUARE> <SQUARE TO MOVE TO>\n"
                    + EscapeSequences.SET_TEXT_COLOR_GREEN + "[CHESS GAME]>>> ");
        }
    }

    public ChessPosition makeChessPosition(char col, int row) throws ResponseException {
        int endingCol;
        switch (col) {
            case 'a' -> endingCol = 1;
            case 'b' -> endingCol = 2;
            case 'c' -> endingCol = 3;
            case 'd' -> endingCol = 4;
            case 'e' -> endingCol = 5;
            case 'f' -> endingCol = 6;
            case 'g' -> endingCol = 7;
            case 'h' -> endingCol = 8;
            default -> throw new ResponseException(400, EscapeSequences.SET_TEXT_COLOR_RED+
                    "Expected: COLUMN<YOUR PIECE'S SQUARE> <SQUARE TO MOVE TO>\n" +EscapeSequences.SET_TEXT_COLOR_GREEN + "[CHESS GAME]>>> ");
        }
        if (row > 8 || row <1){
            System.out.println(row);
            throw new ResponseException(400, EscapeSequences.SET_TEXT_COLOR_RED+ "Expected: ROWS<YOUR PIECE'S SQUARE> <SQUARE TO MOVE TO>\n"
                    +EscapeSequences.SET_TEXT_COLOR_GREEN + "[CHESS GAME]>>> ");
        }
        return new ChessPosition(row,endingCol);
    }

    public String help() {
        return
                EscapeSequences.RESET_TEXT_COLOR + "Possible commands:\n"+
                        EscapeSequences.SET_TEXT_COLOR_BLUE + "redraw " +
                        EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + "- the chess board\n"+
                        EscapeSequences.SET_TEXT_COLOR_BLUE + "leave "+
                        EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + "- the chess game\n"+
                        EscapeSequences.SET_TEXT_COLOR_BLUE + "move <YOUR PIECE'S SQUARE> <SQUARE TO MOVE TO> "+
                        EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + "- takes your turn by moving your piece\n"+
                        EscapeSequences.SET_TEXT_COLOR_BLUE + "resign "+
                        EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY +"- ends the game and you lose\n"+
                        EscapeSequences.SET_TEXT_COLOR_BLUE + "highlight <YOUR PIECE'S SQUARE> "+
                        EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + "- marks the possible moves of a given piece\n"+
                        EscapeSequences.SET_TEXT_COLOR_BLUE + "help "+
                        EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + "- with possible commands\n"+
                        EscapeSequences.SET_TEXT_COLOR_GREEN + "[CHESS GAME]>>> "
                ;
    }

    public String helpObserving() {
        return
                EscapeSequences.RESET_TEXT_COLOR + "As an observer these are your only possible commands:\n"+
                        EscapeSequences.SET_TEXT_COLOR_BLUE + "redraw " +
                        EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + "- the chess board\n"+
                        EscapeSequences.SET_TEXT_COLOR_BLUE + "leave "+
                        EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + "- the chess game\n"+
                        EscapeSequences.SET_TEXT_COLOR_BLUE + "help "+
                        EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + "- with possible commands\n"+
                        EscapeSequences.SET_TEXT_COLOR_GREEN + "[CHESS GAME]>>> "
                ;
    }

    public void setObserving(Boolean observing) {
        this.observing = observing;
    }
}
