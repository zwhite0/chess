package ui;

import sharedserver.exceptions.ResponseException;
import ui.websocket.NotificationHandler;
import ui.websocket.WebSocketFacade;
import websocket.messages.ServerMessage;

import java.util.Scanner;

public class Repl implements NotificationHandler{
    private PreloginUI preloginClient;
    private PostloginUI postloginUI;
    private InGameUI inGameUI;
    private Status status = new Status();
    private AuthTokenHolder authTokenHolder = new AuthTokenHolder();
    private WebSocketFacade ws;

    public Repl(String serverURL) throws ResponseException {
        status.status = "LOGGED_OUT";
        ws = new WebSocketFacade(serverURL,this);
        preloginClient = new PreloginUI(serverURL, status, authTokenHolder);
        inGameUI = new InGameUI(serverURL,status,authTokenHolder,ws, this);
        postloginUI = new PostloginUI(serverURL,status,authTokenHolder,ws,this, inGameUI);
    }

    public void run() {
        System.out.print("Welcome to 240 chess. Type Help to get started.\n"+
                EscapeSequences.SET_TEXT_COLOR_GREEN+"[LOGGED OUT]>>> ");

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            String line = scanner.nextLine();

            if (status.status.equals("LOGGED_OUT")) {
                try {
                    result = preloginClient.eval(line);
                    if (result.startsWith("Error:")){
                        System.out.print(EscapeSequences.SET_TEXT_COLOR_RED +result + "\n"+
                                EscapeSequences.SET_TEXT_COLOR_GREEN + "[LOGGED OUT]>>> ");
                    } else if (!result.equals("quit")) {
                        System.out.print(result);
                    }
                } catch (Throwable e) {
                    var msg = e.toString();
                    System.out.print(EscapeSequences.SET_TEXT_COLOR_RED +msg +
                            EscapeSequences.SET_TEXT_COLOR_GREEN + "[LOGGED OUT]>>> ");
                }
            }
            if (status.status.equals("LOGGED_IN")){
                try {
                    result = postloginUI.eval(line);
                    inGameUI.gameID = postloginUI.gameID;
                    if (!result.equals("quit")) {
                        System.out.print(result);
                    }
                } catch (Throwable e){
                    var msg = e.toString();
                    System.out.print(EscapeSequences.SET_TEXT_COLOR_RED +msg +
                            EscapeSequences.SET_TEXT_COLOR_GREEN + "[LOGGED IN]>>> ");
                }
            }
            if (status.status.equals("IN_GAME")){
                inGameUI.observing = false;
                try {
                    result = inGameUI.eval(line);
                    System.out.print(result);
                } catch (Throwable e){
                    var msg = e.toString();
                    System.out.print(EscapeSequences.SET_TEXT_COLOR_RED +msg +
                            EscapeSequences.SET_TEXT_COLOR_GREEN + "[CHESS GAME]>>> ");
                }
            }
            if (status.status.equals("OBSERVING_GAME")){
                inGameUI.observing = true;
                try{
                    result = inGameUI.eval(line);
                    System.out.print(result);
                } catch (Throwable e){
                    var msg = e.toString();
                    System.out.print(EscapeSequences.SET_TEXT_COLOR_RED +msg +
                            EscapeSequences.SET_TEXT_COLOR_GREEN + "[CHESS GAME]>>> ");
                }
            }
        }
        System.out.println();
    }

    @Override
    public void notify(ServerMessage notification) {
        System.out.print(EscapeSequences.RESET_TEXT_COLOR + notification.getMessage() +
                EscapeSequences.SET_TEXT_COLOR_GREEN + "[CHESS GAME]>>> ");
        if (notification.getServerMessageType().equals(ServerMessage.ServerMessageType.LOAD_GAME)) {
            if (inGameUI.teamColor.equals("white")) {
                PostloginUI.drawWhiteBoard(inGameUI.chessGame.getBoard());
            } else if (inGameUI.teamColor.equals("black")) {
                PostloginUI.drawBlackBoard(inGameUI.chessGame.getBoard());
            }
        }
    }
}
