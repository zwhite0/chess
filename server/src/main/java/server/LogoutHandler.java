package server;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import sharedserver.exceptions.DataAccessException;
import sharedserver.requestsandresults.LogoutRequest;
import sharedserver.requestsandresults.LogoutResult;
import service.UserService;

public class LogoutHandler {

    AuthDAO auths;

    LogoutHandler(AuthDAO auths){
        this.auths = auths;
    }

    public String logoutHandler(String json) throws DataAccessException {
        UserService newLogout = new UserService(null, auths, null);
       var serializer = new Gson();
        LogoutRequest logoutRequest = new LogoutRequest(json);
        LogoutResult logoutResult = newLogout.logout(logoutRequest);
        return serializer.toJson(logoutResult);
    }
}
