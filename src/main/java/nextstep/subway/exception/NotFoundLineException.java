package nextstep.subway.exception;

public class NotFoundLineException extends RuntimeException {
    public NotFoundLineException() {
        super(ErrorMessage.NOT_FOUND_LINE.getMessage());
    }
}
