package nextstep.subway.line.domain.exception;

public class CannotDeleteSectionException extends IllegalSectionOperationException {
    public CannotDeleteSectionException(String message) {
        super(message);
    }
}
