package nextstep.subway.common.exception;

public class NotExistStationException extends BusinessException {
    public NotExistStationException() {
        super("노선에 역이 존재하지 않습니다.");
    }
}
