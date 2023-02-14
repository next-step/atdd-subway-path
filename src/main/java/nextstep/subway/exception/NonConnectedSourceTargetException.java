package nextstep.subway.exception;

public class NonConnectedSourceTargetException extends RuntimeException {

    public static final String MESSAGE = "출발역[%s]과 도착역[%s]이 연결되어 있지 않습니다.";

    public NonConnectedSourceTargetException(String source, String target) {
        super(String.format(MESSAGE, source, target));
    }
}
