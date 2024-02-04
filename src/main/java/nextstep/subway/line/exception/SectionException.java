package nextstep.subway.line.exception;

public class SectionException extends RuntimeException {

    public SectionException() {
    }

    public SectionException(String message) {
        super(message);
    }

    public SectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public SectionException(Throwable cause) {
        super(cause);
    }

    public SectionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
