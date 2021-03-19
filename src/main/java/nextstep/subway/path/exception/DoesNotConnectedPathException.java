package nextstep.subway.path.exception;

public class DoesNotConnectedPathException extends RuntimeException {

    private static final String DOES_NOT_CONNECTED_SOURCE_AND_TARGET_STATION = "출발역과 도착역이 연결되어 있지 않습니다.";

    public DoesNotConnectedPathException() {
        super(DOES_NOT_CONNECTED_SOURCE_AND_TARGET_STATION);
    }
}
