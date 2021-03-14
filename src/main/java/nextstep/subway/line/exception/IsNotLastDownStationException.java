package nextstep.subway.line.exception;

public class IsNotLastDownStationException extends RuntimeException {

    public static final String MESSAGE = "상행역은 하행 종점역이어야 합니다.";

    public IsNotLastDownStationException() {
        super(MESSAGE);
    }

    public IsNotLastDownStationException(String message) {
        super(message);
    }
}
