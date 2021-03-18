package nextstep.subway.path.exception;

public class SameStationPathSearchException extends RuntimeException {

    private static final String EXCEPTION_MESSAGE_INVALID_SOURCE_AND_TARGET_STATION = "출발역과 도착역은 같을 수 없습니다.";

    public SameStationPathSearchException() {
        super(EXCEPTION_MESSAGE_INVALID_SOURCE_AND_TARGET_STATION);
    }
}
