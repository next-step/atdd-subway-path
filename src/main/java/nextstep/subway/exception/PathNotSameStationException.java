package nextstep.subway.exception;

public class PathNotSameStationException extends RuntimeException {
    private static final String MESSAGE = "출발역과 도착역이 같습니다.";

    public PathNotSameStationException() {
        super(MESSAGE);
    }
}
