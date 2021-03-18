package nextstep.subway.station.exception;

public class EqualStationException extends RuntimeException {
    private static final String EQUAL_STATION = "출발역과 도착역이 같으면 경로 조회가 불가합니다.";

    public EqualStationException() {
        super(EQUAL_STATION);
    }
}
