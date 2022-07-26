package nextstep.subway.line.domain.exception;

public abstract class IllegalSectionOperationException extends RuntimeException {
    public IllegalSectionOperationException(String message) {
        super(message);
    }
}
