package nextstep.subway.line.exception;

public class EmptyLineException extends LineDomainException {
    private static final String MESSAGE = "구간이 없는 노선은 존재할 수 없습니다.";

    public EmptyLineException() {
        super(MESSAGE);
    }
}
