package nextstep.subway.path.exception;

public class EqualsStationsException extends RuntimeException {
    public static final String MESSAGE = "출발역과 도착역이 같습니다.";

    public EqualsStationsException() {
        super(MESSAGE);
    }
}
