package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
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
            case RESIGN -> resign(command.getAuthToken(), command.getGameID());
        }
    }

    private void connect(String authToken, Session session, int gameID) throws IOException, DataAccessException {
        AuthData auth = auths.getAuth(authToken);
        GameData game = games.getGame(gameID);
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
        String message;
        if (visitorName.equalsIgnoreCase(game.whiteUsername())) {
            message = String.format("%s has joined the game as the white player", visitorName);
        } else if(visitorName.equalsIgnoreCase(game.blackUsername())) {
            message = String.format("%s has joined the game as the black player", visitorName);
        } else {
            message = String.format("%s has joined the game as an observer", visitorName);
        }
        ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        notification.setMessage(message);
        connections.broadcast(visitorName, notification, gameIdToSessions.get(gameID));
    }

    private void leave(String authToken, int gameID) throws IOException, DataAccessException {
        AuthData auth = auths.getAuth(authToken);
        String visitorName = auth.username();
        String message = String.format("%s has left the game", visitorName);
        ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        GameData game = games.getGame(gameID);
        GameData updatedGame;
        if (visitorName.equalsIgnoreCase(game.whiteUsername())){
            updatedGame =new GameData(gameID,null,game.blackUsername(),game.gameName(),game.game());
        } else {
            updatedGame =new GameData(gameID,game.whiteUsername(),null,game.gameName(),game.game());
        }
        games.updateGame(updatedGame);
        notification.setMessage(message);
        connections.broadcast(visitorName, notification, gameIdToSessions.get(gameID));
        connections.remove(visitorName);
    }

    private void move(String authToken, ChessMove chessMove, int gameID) throws DataAccessException, IOException, InvalidMoveException {
        AuthData auth = auths.getAuth(authToken);
        String visitorName = auth.username();
        String alphabet = "aabcdefgh";
        ChessPosition start = chessMove.getStartPosition();
        ChessPosition end = chessMove.getEndPosition();
        int startCol = start.getColumn();
        int startRow = start.getRow();
        int endCol = end.getColumn();
        int endRow = end.getRow();

        try {
            String message = String.format("%s has made the move %c%d %c%d", visitorName,
                    alphabet.charAt(startCol), startRow, alphabet.charAt(endCol), endRow);
            GameData game = games.getGame(gameID);
            ChessGame.TeamColor opposingTeamColor;
            String opponentName;
            if (visitorName.equalsIgnoreCase(game.whiteUsername())){
                opposingTeamColor = ChessGame.TeamColor.BLACK;
                opponentName = game.blackUsername();
            } else {
                opposingTeamColor = ChessGame.TeamColor.WHITE;
                opponentName = game.whiteUsername();
            }
            game.game().makeMove(chessMove);
            games.updateGame(game);
            ServerMessage update = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
            update.setMessage(message);
            update.setUpdatedGame(game.game());
            connections.broadcast(visitorName, update, gameIdToSessions.get(gameID));
            if (game.game().isInCheck(opposingTeamColor)){
                ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
                notification.setMessage(String.format("%s is in check", opponentName));
                notification.setCheck(true);
                connections.broadcast(visitorName,notification,gameIdToSessions.get(gameID));
            }
        } catch (InvalidMoveException ex){
            ServerMessage error = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            error.setMessage("Invalid Move");
            connections.sendMessage(visitorName,error );
        }
    }

    private void resign(String authToken, int gameID) throws DataAccessException, IOException {
        AuthData auth = auths.getAuth(authToken);
        String visitorName = auth.username();
        GameData game = games.getGame(gameID);
        String winner;
        String message;
        if (visitorName.equalsIgnoreCase(game.whiteUsername())){
            winner = game.blackUsername();
        } else {
            winner = game.whiteUsername();
        }
        if (winner == null){
            message = String.format("Game over. %s has resigned.", visitorName);
        } else {
            message = String.format("Game over. %s has resigned. %s wins!", visitorName, winner);
        }
        ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        notification.setMessage(message);
        notification.setEndGame(true);
        connections.broadcast(visitorName, notification, gameIdToSessions.get(gameID));
    }

}
