package nextstep.subway.error.exception;

public class EmptySectionException extends RuntimeException {

    private static final String MESSAGE = "노선의 구간이 없습니다.";

    public EmptySectionException() {
        super(MESSAGE);
    }
}
