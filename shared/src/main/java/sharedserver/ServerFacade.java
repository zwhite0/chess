package sharedserver;

import com.google.gson.Gson;
import sharedserver.exceptions.*;
import sharedserver.requestsandresults.*;

import java.io.*;
import java.net.*;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws ResponseException {
        var path = "/user";
        return this.makeRequest("POST",path,registerRequest, RegisterResult.class, null);
    }

    public LoginResult login(LoginRequest loginRequest) throws ResponseException {
        var path = "/session";
        return this.makeRequest("POST",path,loginRequest, LoginResult.class, null);
    }

    public LogoutResult logout(LogoutRequest logoutRequest) throws ResponseException{
        var path = "/session";
        return this.makeRequest("DELETE",path,logoutRequest, LogoutResult.class, logoutRequest.authToken());
    }

    public ClearResult clear(ClearRequest clearRequest) throws ResponseException {
        var path = "/db";
        return this.makeRequest("DELETE",path,clearRequest,ClearResult.class,null);
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws ResponseException {
        var path = "/game";
        return this.makeRequest("POST",path,createGameRequest,CreateGameResult.class, createGameRequest.authToken());
    }

    public JoinGameResult joinGame(JoinGameRequest joinGameRequest) throws ResponseException{
        var path = "/game";
        return this.makeRequest("PUT",path,joinGameRequest, JoinGameResult.class, joinGameRequest.authToken());
    }

    public ListGamesResult listGames(ListGamesRequest listGamesRequest) throws ResponseException{
        var path = "/game";
        return this.makeRequest("GET",path,listGamesRequest,ListGamesResult.class, listGamesRequest.authToken());
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String authToken) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);
            if (authToken != null) {
                http.setRequestProperty("authorization", authToken);
            }
            if (!method.equals("GET")) {
                writeBody(request, http);
            }
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (ResponseException ex){
            throw new ResponseException(ex.statusCode(),ex.getMessage());
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw ResponseException.fromJson(respErr,status);
                }
            }

            throw new ResponseException(status, "other failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }


}
