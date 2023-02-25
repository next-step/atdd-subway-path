package nextstep.subway.exception;

public class NotEqualLastStationException extends IllegalArgumentException {

    private static final String NOT_EQUAL_LAST_STATION = "새로운 구간의 상행역은 해당 노선에 등록되어있는 하행 종점역이어야 합니다.";

    public NotEqualLastStationException() {
        super(NOT_EQUAL_LAST_STATION);
    }
}
