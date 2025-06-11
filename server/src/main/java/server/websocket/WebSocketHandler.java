package server.websocket;

import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import sharedserver.exceptions.DataAccessException;
import sharedserver.exceptions.ResponseException;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    UserDAO users;
    AuthDAO auths;
    GameDAO games;

    Map<Integer, Set<Session>> gameIdToSessions = new ConcurrentHashMap<>();


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
    public void onMessage(Session session, String message) throws IOException, DataAccessException, InvalidMoveException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> connect(command.getAuthToken(), session, command.getGameID());
            case LEAVE -> leave(command.getAuthToken(), command.getGameID());
            case MAKE_MOVE -> move(command.getAuthToken(), command.getMove(), command.getGameID());
        }
    }

    private void connect(String authToken, Session session, int gameID) throws IOException, DataAccessException {
        AuthData auth = auths.getAuth(authToken);
        String visitorName = auth.username();
        if (gameIdToSessions.get(gameID) == null){
            Set<Session> sessions = new HashSet<>();
            sessions.add(session);
            gameIdToSessions.put(gameID,sessions);
        } else {
            Set<Session> sessions = gameIdToSessions.get(gameID);
            sessions.add(session);
        }
        connections.add(visitorName, session);
        String message = String.format("%s has joined the game", visitorName);
        ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        notification.setMessage(message);
        connections.broadcast(visitorName, notification, gameIdToSessions.get(gameID));
    }

    private void leave(String authToken, int gameID) throws IOException, DataAccessException {
        AuthData auth = auths.getAuth(authToken);
        String visitorName = auth.username();
        String message = String.format("%s has left the game", visitorName);
        ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        notification.setMessage(message);
        connections.broadcast(visitorName, notification, gameIdToSessions.get(gameID));
        connections.remove(visitorName);
    }

    private void move(String authToken, ChessMove chessMove, int gameID) throws DataAccessException, IOException, InvalidMoveException {
        AuthData auth = auths.getAuth(authToken);
        String visitorName = auth.username();
        String message = String.format("%s has made a move", visitorName);
        GameData game = games.getGame(gameID);
        game.game().makeMove(chessMove);
        games.updateGame(game);
        ServerMessage update = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        update.setMessage(message);
        connections.broadcast(visitorName, update, gameIdToSessions.get(gameID));
    }

}
