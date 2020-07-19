package nextstep.subway.path.exception;

public class findNonExistSourceOrTargetException extends RuntimeException {
    public findNonExistSourceOrTargetException() {
    }

    public findNonExistSourceOrTargetException(String message) {
        super(message);
    }

    public findNonExistSourceOrTargetException(String message, Throwable cause) {
        super(message, cause);
    }

    public findNonExistSourceOrTargetException(Throwable cause) {
        super(cause);
    }

    public findNonExistSourceOrTargetException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
