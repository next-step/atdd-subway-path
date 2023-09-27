package nextstep.subway.common.exception;

public class DisconnectedPathException extends BusinessException {
    public DisconnectedPathException() {
        super("경로가 연결되어있지 않습니다.");
    }
}
