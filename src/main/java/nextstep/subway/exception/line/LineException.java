package nextstep.subway.exception.line;

public class LineException extends RuntimeException {
    private static String DEFAULT_MESSAGE = "노선 에러 : ";

    public LineException() {
        super(DEFAULT_MESSAGE);
    }

    public LineException(String message) {
        super(message);
    }

    public LineException(String message, Throwable cause) {
        super(message, cause);
    }

    public LineException(Throwable cause) {
        super(cause);
    }

    protected LineException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
