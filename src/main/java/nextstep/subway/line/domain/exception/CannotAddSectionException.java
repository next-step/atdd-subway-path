package nextstep.subway.line.domain.exception;

public class CannotAddSectionException extends IllegalSectionOperationException {
    public CannotAddSectionException(String message) {
        super(message);
    }
}
