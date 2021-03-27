package nextstep.subway.exception;

public class LineDisconnectedException extends BusinessException{

    private static final String ERROR_MESSAGE = "출발역과 도착역이 연결되어 있지 않습니다";

    public LineDisconnectedException() {
        super(ERROR_MESSAGE);
    }
}
