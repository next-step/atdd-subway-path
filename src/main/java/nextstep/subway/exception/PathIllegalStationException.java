package nextstep.subway.exception;

public class PathIllegalStationException extends RuntimeException {

    private final static String message = "출발역과 도착역이 연결되어 있지 않거나, 존재하지 않는 역입니다.";
    public PathIllegalStationException() {
        super(message);
    }
}
