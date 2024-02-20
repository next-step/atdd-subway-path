package nextstep.subway.exception.station;

public class StationException extends RuntimeException {
    private static String DEFAULT_MESSAGE = "지하철 역 에러 : ";

    public StationException() {
        super();
    }

    public StationException(String message) {
        super(String.format("%s %s", DEFAULT_MESSAGE, message));
    }

    public StationException(String message, Throwable cause) {
        super(message, cause);
    }

    public StationException(Throwable cause) {
        super(cause);
    }

    protected StationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
