package nextstep.subway.exception;

public class NotEqualsLastStationException extends RuntimeException {
    public NotEqualsLastStationException() {
        super("신규 구간의 상행역과 현재 라인의 하행역이 동일하지 않습니다.");
    }
}