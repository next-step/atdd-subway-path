package nextstep.subway.exception;

public class NoLastStationException extends RuntimeException {

    private static final String NO_LAST_STATION_EXCEPTION = "지우려는 역이 하행 종점역이 아닙니다. (마지막 역만 제거 가능합니다)";

    public NoLastStationException() {
        super(NO_LAST_STATION_EXCEPTION);
    }

}