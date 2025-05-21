package server;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import service.LogoutRequest;
import service.LogoutResult;
import service.UserService;

public class LogoutHandler {

    AuthDAO auths;

    LogoutHandler(AuthDAO auths){
        this.auths = auths;
    }

    public String logoutHandler(String json) {
        UserService newLogout = new UserService(null, auths, null);
        var serializer = new Gson();
        LogoutRequest logoutRequest = serializer.fromJson(json, LogoutRequest.class);
        LogoutResult logoutResult = newLogout.logout(logoutRequest);
        return serializer.toJson(logoutResult);
    }
}
