package nextstep.subway.exception;

public class NotConnectedPathException extends PathException {

    private static final String ERROR_MESSAGE = "출발역과 도착역이 연결되어 있지 않습니다.";

    public NotConnectedPathException() {
        super(ERROR_MESSAGE);
    }
}
