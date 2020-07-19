package nextstep.subway.path.exception;

public class NotConnectedSourceAndTargetException extends RuntimeException {
    public NotConnectedSourceAndTargetException() {
    }

    public NotConnectedSourceAndTargetException(String message) {
        super(message);
    }

    public NotConnectedSourceAndTargetException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotConnectedSourceAndTargetException(Throwable cause) {
        super(cause);
    }

    public NotConnectedSourceAndTargetException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
