package nextstep.subway.applicaion.path.exception;

public class IllegalSourceTargetException extends PathException {
    public IllegalSourceTargetException() {
        super("출발역과 도착역이 동일합니다.");
    }
}
