package nextstep.subway.common.exception;

public class ValidationError extends BadRequestException {
    public ValidationError(final String message) {
        super(message);
    }

    public ValidationError(final String message, final Throwable cause) {
        super(message, cause);
    }
}
