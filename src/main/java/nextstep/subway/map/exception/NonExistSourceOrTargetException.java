package nextstep.subway.map.exception;

public class NonExistSourceOrTargetException extends RuntimeException {
    public NonExistSourceOrTargetException() {
    }

    public NonExistSourceOrTargetException(String message) {
        super(message);
    }

    public NonExistSourceOrTargetException(String message, Throwable cause) {
        super(message, cause);
    }

    public NonExistSourceOrTargetException(Throwable cause) {
        super(cause);
    }

    public NonExistSourceOrTargetException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
