package server;

import com.google.gson.Gson;
import service.RegisterRequest;
import service.RegisterResult;
import service.UserService;

public class RegisterHandler {

    private String registerHandler(String json) {
        UserService newRegister = new UserService();
        var serializer = new Gson();
        RegisterRequest registerRequest = serializer.fromJson(json,RegisterRequest.class);
        RegisterResult registerResult = newRegister.register(registerRequest);
        return serializer.toJson(registerResult);
    }
}
