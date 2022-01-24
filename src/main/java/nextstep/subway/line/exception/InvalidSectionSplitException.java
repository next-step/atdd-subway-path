package nextstep.subway.line.exception;

public class InvalidSectionSplitException extends LineDomainException {
    private static final String MESSAGE = "분할하는 구간의 상행선이 같거나, 하행선이 같아야 합니다.";

    public InvalidSectionSplitException() {
        super(MESSAGE);
    }
}
