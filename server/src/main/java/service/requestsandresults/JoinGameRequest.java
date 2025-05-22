package service.requestsandresults;

public record JoinGameRequest(String authToken, String playerColor, Integer gameID) {
}
