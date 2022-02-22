package nextstep.subway.path.exception;

public class InvalidSourceTargetException extends PathDomainException {
    private static final String MESSAGE = "출발역과 도착역은 같을 수 없습니다.";

    public InvalidSourceTargetException() {
        super(MESSAGE);
    }
}
