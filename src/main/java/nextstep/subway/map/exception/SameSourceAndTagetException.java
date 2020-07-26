package nextstep.subway.map.exception;

public class SameSourceAndTagetException extends RuntimeException {
    public SameSourceAndTagetException() {
    }

    public SameSourceAndTagetException(String message) {
        super(message);
    }

    public SameSourceAndTagetException(String message, Throwable cause) {
        super(message, cause);
    }

    public SameSourceAndTagetException(Throwable cause) {
        super(cause);
    }

    public SameSourceAndTagetException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
