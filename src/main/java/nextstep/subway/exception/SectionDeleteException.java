package nextstep.subway.exception;

public class SectionDeleteException extends RuntimeException {
    public SectionDeleteException(ErrorType errorType) {
        super(errorType.getMessage());
    }
}
