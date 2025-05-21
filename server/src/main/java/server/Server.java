package server;

import com.google.gson.Gson;
import service.AlreadyTakenException;
import service.BadRequestException;
import service.ErrorResponse;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", (request, response) ->{
            RegisterHandler handler = new RegisterHandler();
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

        Spark.delete("/db", (request, response) -> {
            ClearHandler handler = new ClearHandler();
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
