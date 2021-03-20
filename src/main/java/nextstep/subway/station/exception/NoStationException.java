package nextstep.subway.station.exception;

public class NoStationException extends RuntimeException {
    private static final String NO_STATION = "출발역 혹은 도착역 정보가 없습니다.";

    public NoStationException() {
        super(NO_STATION);
    }
}
