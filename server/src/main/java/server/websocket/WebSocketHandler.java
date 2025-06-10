package server.websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.*;
import model.AuthData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import sharedserver.exceptions.DataAccessException;
import sharedserver.exceptions.ResponseException;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Timer;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    UserDAO users;
    AuthDAO auths;
    GameDAO games;

    {
        try {
            users = new SQLUserDAO();
            auths = new SQLAuthDAO();
            games = new SQLGameDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> connect(command.getAuthToken(), session);
            case LEAVE -> leave(command.getAuthToken());
            case MAKE_MOVE -> move(command.getAuthToken(), command.getMove());
        }
    }

    private void connect(String authToken, Session session) throws IOException, DataAccessException {
        AuthData auth = auths.getAuth(authToken);
        String visitorName = auth.username();
        connections.add(visitorName, session);
        String message = String.format("%s has joined the game", visitorName);
        ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        notification.setMessage(message);
        connections.broadcast(visitorName, notification);
    }

    private void leave(String authToken) throws IOException, DataAccessException {
        AuthData auth = auths.getAuth(authToken);
        String visitorName = auth.username();
        connections.remove(visitorName);
        String message = String.format("%s has left the game", visitorName);
        ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        notification.setMessage(message);
        connections.broadcast(visitorName, notification);
    }

    private void move(String authToken, ChessMove chessMove){

    }

//    public void makeNoise(String petName, String sound) throws ResponseException {
//        try {
//            var message = String.format("%s says %s", petName, sound);
//            var notification = new Notification(Notification.Type.NOISE, message);
//            connections.broadcast("", notification);
//        } catch (Exception ex) {
//            throw new ResponseException(500, ex.getMessage());
//        }
//    }
}
