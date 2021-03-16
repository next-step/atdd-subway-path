package nextstep.subway.path.exception;

public class DoseNotConnectedException extends RuntimeException {
    public static final String MESSAGE = "출발역과 도착역이 연결이 되어 있지 않습니다.";

    public DoseNotConnectedException() {
        super(MESSAGE);
    }
}
