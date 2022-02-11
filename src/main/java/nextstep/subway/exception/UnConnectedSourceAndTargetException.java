package nextstep.subway.exception;

public class UnConnectedSourceAndTargetException extends RuntimeException {
    private static final String MESSAGE = "출발역과 도착역이 연결이 되어 있지 않은 경우 최단거리를 구할 수 없습니다.";

    public UnConnectedSourceAndTargetException() {
        super(MESSAGE);
    }
}
