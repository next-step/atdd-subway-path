package nextstep.subway.exception.line;

public class LineSectionException extends RuntimeException {
    public LineSectionException() {
        super();
    }

    public LineSectionException(String message) {
        super(message);
    }

    public LineSectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public LineSectionException(Throwable cause) {
        super(cause);
    }

    protected LineSectionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
