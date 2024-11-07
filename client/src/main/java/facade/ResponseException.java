package facade;

public class ResponseException extends Exception{
    public int errorCode;

    public ResponseException(int errorCode, String message) {
        super(String.format("%s %s", "Error:", message));
        this.errorCode = errorCode;
    }

}