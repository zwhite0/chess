package server;

import com.google.gson.Gson;
import dataaccess.*;
import service.*;
import service.requestsandresults.ErrorResponse;
import spark.*;

public class Server {

    UserDAO users = new MemoryUserDAO();
    AuthDAO auths =  new MemoryAuthDAO();
    GameDAO games = new MemoryGameDAO();

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", (request, response) ->{
            RegisterHandler handler = new RegisterHandler(users, auths);
            try {
                return handler.registerHandler(request.body());
            } catch (AlreadyTakenException e){
                return catchExceptions(response, 403,"Error: already taken");
            } catch (BadRequestException e){
                return catchExceptions(response, 400,"Error: bad request");
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
            }
        });

        Spark.delete("/session", (request, response) -> {
            LogoutHandler handler = new LogoutHandler(auths);
            try {
                return handler.logoutHandler(request.headers("authorization"));
            } catch (UnauthorizedException e){
                return catchExceptions(response, 401,"Error: unauthorized");
            }
        });

        Spark.delete("/db", (request, response) -> {
            ClearHandler handler = new ClearHandler(users, auths, games);
            return handler.clearHandler(request.body());
        });

        Spark.post("/game", (request, response) -> {
           CreateGameHandler handler = new CreateGameHandler(auths, games);
           try {
               return handler.createGameHandler(request.headers("authorization"),request.body());
           } catch (UnauthorizedException e){
               return catchExceptions(response, 401,"Error: unauthorized");
           } catch (BadRequestException e){
               return catchExceptions(response, 400,"Error: bad request");
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
            }
        });

        Spark.get("/game", (request, response) -> {
            ListGamesHandler handler = new ListGamesHandler(auths, games);
            try {
                return handler.listGamesHandler(request.headers("authorization"));
            } catch (UnauthorizedException e){
                return catchExceptions(response, 401,"Error: unauthorized");
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
        ErrorResponse message = new ErrorResponse("Error: unauthorized");
        return gson.toJson(message);
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
