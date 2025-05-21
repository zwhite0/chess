package server;

import com.google.gson.Gson;
import service.*;

public class ClearHandler {

    public String clearHandler(String json){
        UserService clear = new UserService();
        var serializer = new Gson();
        ClearRequest clearRequest = serializer.fromJson(json,ClearRequest.class);
        ClearResult clearResult = clear.clear(clearRequest);
        return serializer.toJson(clearResult);
    }
}
