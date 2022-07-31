package nextstep.subway.exception;

public class NotConnectedException extends RuntimeException {

    public NotConnectedException() {
        super("입력한 두개의 역이 연결되어 있지 않습니다.");
    }

}
