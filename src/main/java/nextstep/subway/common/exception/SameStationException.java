package nextstep.subway.common.exception;

public class SameStationException extends BusinessException {

    public SameStationException() {
        super("출발역과 도착역이 같습니다.");
    }
}
