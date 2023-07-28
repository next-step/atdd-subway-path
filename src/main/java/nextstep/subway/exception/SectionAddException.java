package nextstep.subway.exception;

public class SectionAddException extends RuntimeException {
    public SectionAddException(ErrorType errorType) {
        super(errorType.getMessage());
    }
}
