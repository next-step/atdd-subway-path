package nextstep.subway.exception;

public class InvalidRemoveSectionException extends IllegalArgumentException {
    public InvalidRemoveSectionException() {
        super("there are no routes that can be deleted.");
    }
}
