package facade;

public class ResponseException extends Exception {
    private final int errorCode;

    public ResponseException(int errorCode, String message) {
        super(String.format("Error %d: %s", errorCode, message));
        this.errorCode = errorCode;
    }

    public ResponseException(String message) {
        super("Error: " + message);
        this.errorCode = -1; // Default error code if not provided
    }

    public int getErrorCode() {
        return errorCode;
    }
}
