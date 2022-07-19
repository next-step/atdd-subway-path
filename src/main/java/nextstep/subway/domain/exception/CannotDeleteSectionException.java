package nextstep.subway.domain.exception;

public class CannotDeleteSectionException extends RuntimeException {
    public CannotDeleteSectionException(String message) {
        super(message);
    }
}
