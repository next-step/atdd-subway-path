package nextstep.subway.exception.path;

public class PathException extends RuntimeException {
    private static String DEFAULT_MESSAGE = "경로 에러 : ";

    public PathException() {
        super();
    }

    public PathException(String message) {
        super(String.format("%s %s", DEFAULT_MESSAGE, message));
    }

    public PathException(String message, Throwable cause) {
        super(message, cause);
    }

    public PathException(Throwable cause) {
        super(cause);
    }

    protected PathException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
