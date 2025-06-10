package server;

import com.google.gson.Gson;
import dataaccess.*;
import sharedserver.exceptions.AlreadyTakenException;
import sharedserver.exceptions.BadRequestException;
import sharedserver.exceptions.DataAccessException;
import sharedserver.exceptions.UnauthorizedException;
import sharedserver.requestsandresults.ErrorResponse;
import spark.*;
import server.websocket.WebSocketHandler;

public class Server {

    UserDAO users;
    AuthDAO auths;
    GameDAO games;
    WebSocketHandler webSocketHandler = new WebSocketHandler();

    {
        try {
            users = new SQLUserDAO();
            auths = new SQLAuthDAO();
            games = new SQLGameDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }


    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.webSocket("/ws", webSocketHandler);

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", (request, response) ->{
            RegisterHandler handler = new RegisterHandler(users, auths);
            try {
                return handler.registerHandler(request.body());
            } catch (AlreadyTakenException e){
                return catchExceptions(response, 403,"Error: already taken");
            } catch (BadRequestException e){
                return catchExceptions(response, 400,"Error: bad request");
            } catch (DataAccessException e){
                return catchExceptions(response, 500, "Error: bad data access");
            }
        });

        Spark.post("/session", (request, response) -> {
            LoginHandler handler = new LoginHandler(users, auths);
            try {
                return handler.loginHandler(request.body());
            } catch (BadRequestException e){
                return catchExceptions(response, 400,"Error: bad request");
            } catch (UnauthorizedException e){
                return catchExceptions(response, 401,"Error: unauthorized");
            } catch (DataAccessException e){
                return catchExceptions(response, 500, "Error: bad data access");
            }
        });

        Spark.delete("/session", (request, response) -> {
            LogoutHandler handler = new LogoutHandler(auths);
            try {
                return handler.logoutHandler(request.headers("authorization"));
            } catch (UnauthorizedException e){
                return catchExceptions(response, 401,"Error: unauthorized");
            } catch (DataAccessException e){
                return catchExceptions(response, 500, "Error: bad data access");
            }
        });

        Spark.delete("/db", (request, response) -> {
            ClearHandler handler = new ClearHandler(users, auths, games);
            try {
                return handler.clearHandler(request.body());
            } catch (DataAccessException e){
                return catchExceptions(response, 500, "Error: bad data access");
            }
        });

        Spark.post("/game", (request, response) -> {
           CreateGameHandler handler = new CreateGameHandler(auths, games);
           try {
               return handler.createGameHandler(request.headers("authorization"),request.body());
           } catch (UnauthorizedException e){
               return catchExceptions(response, 401,"Error: unauthorized");
           } catch (BadRequestException e){
               return catchExceptions(response, 400,"Error: bad request");
           } catch (DataAccessException e){
               return catchExceptions(response, 500, "Error: bad data access");
           }
        });

        Spark.put("/game", (request, response) -> {
            JoinGameHandler handler = new JoinGameHandler(auths, games);
            try {
                return handler.joinGameHandler(request.headers("authorization"), request.body());
            } catch (UnauthorizedException e){
                return catchExceptions(response, 401,"Error: unauthorized");
            } catch (BadRequestException e){
                return catchExceptions(response, 400,"Error: bad request");
            } catch (AlreadyTakenException e){
                return catchExceptions(response, 403,"Error: already taken");
            } catch (DataAccessException e){
                return catchExceptions(response, 500, "Error: bad data access");
            }
        });

        Spark.get("/game", (request, response) -> {
            ListGamesHandler handler = new ListGamesHandler(auths, games);
            try {
                return handler.listGamesHandler(request.headers("authorization"));
            } catch (UnauthorizedException e){
                return catchExceptions(response, 401,"Error: unauthorized");
            } catch (DataAccessException e){
                return catchExceptions(response, 500, "Error: bad data access");
            }
        });

        //This line initializes the server and can be removed once you have a functioning endpoint
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    private static String catchExceptions(spark.Response response, int statusCode, String errorMessage){
        response.status(statusCode);
        Gson gson = new Gson();
        ErrorResponse message = new ErrorResponse(errorMessage);
        System.out.println(gson.toJson(message));
        return gson.toJson(message);
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
