package nextstep.subway.domain.exception;

public class NotConnectedPathException extends IllegalArgumentException {
    private static final String MESSAGE = "출발역과 도착역이 연결되어 있지 않습니다. 출발역=%s, 도착역=%s";

    public NotConnectedPathException(String sourceName, String targetName) {
        super(String.format(MESSAGE, sourceName, targetName));
    }
}
