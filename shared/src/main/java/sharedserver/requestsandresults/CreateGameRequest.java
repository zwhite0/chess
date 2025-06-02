package sharedserver.requestsandresults;

public record CreateGameRequest(String gameName, String authToken) {
}
