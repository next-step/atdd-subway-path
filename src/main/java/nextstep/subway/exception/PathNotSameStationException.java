package nextstep.subway.exception;

public class PathNotSameStationException extends RuntimeException {
    private final static String message = "출발역과 도착역이 같습니다.";

    public PathNotSameStationException() {
        super(message);
    }
}
