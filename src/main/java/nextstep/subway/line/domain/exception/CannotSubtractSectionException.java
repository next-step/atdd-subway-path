package nextstep.subway.line.domain.exception;

public class CannotSubtractSectionException extends IllegalSectionOperationException {
    public CannotSubtractSectionException(String message) {
        super(message);
    }
}
