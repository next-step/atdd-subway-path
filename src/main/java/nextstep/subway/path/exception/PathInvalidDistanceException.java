package nextstep.subway.path.exception;

public class PathInvalidDistanceException extends PathDomainException {
    private static final String MESSAGE = "경로의 길이는 음수가 될 수 없습니다.";

    public PathInvalidDistanceException() {
        super(MESSAGE);
    }
}
