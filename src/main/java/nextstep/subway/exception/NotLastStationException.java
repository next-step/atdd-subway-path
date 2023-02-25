package nextstep.subway.exception;

public class NotLastStationException extends IllegalArgumentException {

    private static final String NOT_THE_LAST_STATION = "지하철 노선에 등록된 하행 종점역만 제거할 수 있습니다.";

    public NotLastStationException() {
        super(NOT_THE_LAST_STATION);
    }
}
