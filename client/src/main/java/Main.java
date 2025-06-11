import chess.*;
import sharedserver.exceptions.ResponseException;
import ui.Repl;

public class Main {
    public static void main(String[] args) throws ResponseException {

        int port = (args.length > 0) ? Integer.parseInt(args[0]) : 8080;
        String serverUrl = "http://localhost:" + port;

        new Repl(serverUrl).run();
    }
}
