package nextstep.subway.exception;

public class CantGetPathBySameStationException extends RuntimeException {
    private static final String MESSAGE = "출발역과 도착역이 같은 경우 최단거리를 구할 수 없습니다.";
    public CantGetPathBySameStationException() {
        super(MESSAGE);
    }
}
