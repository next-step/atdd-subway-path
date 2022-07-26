package nextstep.subway.line.domain.exception;

public class CannotCombineSectionException extends IllegalSectionOperationException {
    public CannotCombineSectionException(String message) {
        super(message);
    }
}
