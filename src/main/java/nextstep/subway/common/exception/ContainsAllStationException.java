package nextstep.subway.common.exception;

public class ContainsAllStationException extends BusinessException {
    public ContainsAllStationException() {
        super("이미 노선에 모든 역이 존재합니다.");
    }
}
