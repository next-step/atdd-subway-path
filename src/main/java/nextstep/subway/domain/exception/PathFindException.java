package nextstep.subway.domain.exception;

public class PathFindException extends IllegalArgumentException {
    private static final String MESSAGE = "경로를 찾을 수 없습니다.";

    public PathFindException() {
        super(MESSAGE);
    }
}
