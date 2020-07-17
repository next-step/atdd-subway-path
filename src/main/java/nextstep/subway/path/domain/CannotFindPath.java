package nextstep.subway.path.domain;

public class CannotFindPath extends RuntimeException {
    public CannotFindPath() {
        super("Cannot find path");
    }

    public CannotFindPath(final String message) {
        super(message);
    }

    public CannotFindPath(final String message, final Throwable cause) {
        super(message, cause);
    }

    public CannotFindPath(final Throwable cause) {
        super(cause);
    }

    public CannotFindPath(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
