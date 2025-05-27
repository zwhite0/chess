package dataaccess;

public class ResponseException extends RuntimeException {
    public ResponseException(int errorMessage, String message) {
        super(message);
    }
}
