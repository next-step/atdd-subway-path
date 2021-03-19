package nextstep.subway.path.exception;

public class NotConnectedException extends RuntimeException {
    public NotConnectedException() {
        super("출발역과 도착역이 연결이 되어 있지 않습니다");
    }
}
