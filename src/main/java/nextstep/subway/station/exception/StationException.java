package nextstep.subway.station.exception;

public class StationException extends RuntimeException {
    public StationException() {
    }

    public StationException(String message) {
        super(message);
    }

    public StationException(String message, Throwable cause) {
        super(message, cause);
    }

    public StationException(Throwable cause) {
        super(cause);
    }

    public StationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
