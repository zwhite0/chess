package service.RequestsAndResults;

public record JoinGameRequest(String authToken, String playerColor, Integer gameID) {
}
