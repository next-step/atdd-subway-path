package nextstep.subway.path.domain;

public class CannotFindPathException extends RuntimeException {
    public CannotFindPathException() {
        super("Cannot find path");
    }

    public CannotFindPathException(final String message) {
        super(message);
    }

    public CannotFindPathException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public CannotFindPathException(final Throwable cause) {
        super(cause);
    }

    public CannotFindPathException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
