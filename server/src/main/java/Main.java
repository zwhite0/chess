import chess.*;
import server.Server;

public class Main {
    public static void main(String[] args) {
        Server myServer = new Server();
        myServer.run(8080);
    }
}