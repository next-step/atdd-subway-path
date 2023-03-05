package nextstep.subway.exception;

public class PathNotConnectedException extends RuntimeException {
    private static final String MESSAGE = "두 역이 연결돼 있지 않습니다.";
    public PathNotConnectedException() {
        super(MESSAGE);
    }
}
