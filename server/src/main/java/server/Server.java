package server;

import com.google.gson.Gson;
import dataaccess.*;
import service.AlreadyTakenException;
import service.BadRequestException;
import service.ErrorResponse;
import service.UnauthorizedException;
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
                response.status(403);
                Gson gson = new Gson();
                ErrorResponse message = new ErrorResponse("Error: already taken");
                String json = gson.toJson(message);
                return json;
            } catch (BadRequestException e){
                response.status(400);
                Gson gson = new Gson();
                ErrorResponse message = new ErrorResponse("Error: bad request");
                String json = gson.toJson(message);
                return json;
            }
        });

        Spark.post("/session", (request, response) -> {
            LoginHandler handler = new LoginHandler(users, auths);
            try {
                return handler.loginHandler(request.body());
            } catch (BadRequestException e){
                response.status(400);
                Gson gson = new Gson();
                ErrorResponse message = new ErrorResponse("Error: bad request");
                String json = gson.toJson(message);
                return json;
            } catch (UnauthorizedException e){
                response.status(401);
                Gson gson = new Gson();
                ErrorResponse message = new ErrorResponse("Error: unauthorized");
                String json = gson.toJson(message);
                return json;
            }
        });

        Spark.delete("/db", (request, response) -> {
            ClearHandler handler = new ClearHandler(users, auths, games);
            return handler.clearHandler(request.body());
        });

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
