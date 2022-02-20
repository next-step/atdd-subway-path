package nextstep.subway.error.exception;

public class SourceAndTargetSameException extends RuntimeException {
    private static final String MESSAGE = "출발역과 도착역이 같습니다.";

    public SourceAndTargetSameException() {
        super(MESSAGE);
    }
}
