package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();


    public void add(String visitorName, Session session) {
        Connection existing = connections.get(visitorName);

        if (existing != null && !existing.session.isOpen()) {
            connections.remove(visitorName); // clear stale connection
        }

        var connection = new Connection(visitorName, session);
        connections.put(visitorName, connection);
    }

    public void remove(String visitorName) {
        connections.remove(visitorName);
    }

    public void broadcast(String excludeVisitorName, ServerMessage message, Set<Connection> sessions) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : sessions) {
            if (c.session.isOpen()) {
                if (!c.visitorName.equals(excludeVisitorName)) {
                    c.send(message.toString());
                }
            } else {
                removeList.add(c);
            }
        }
        if (message.isEndGame()){
            for (Connection session : sessions){
                session.session.close();
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.remove(c.visitorName);
        }
    }

    public void broadcastAll(ServerMessage message, Set<Connection> sessions) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : sessions) {
            if (c.session.isOpen()) {
                c.send(message.toString());
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.remove(c.visitorName);
        }
    }

    public void sendMessage(Session session, ServerMessage message) throws IOException {
        for (var c : connections.values()){
            if (c.session.equals(session)){
                c.send(message.toString());
            }
        }
    }
}