package nextstep.subway.global.exception;

public class InsufficientStationException extends RuntimeException {

    private static final String MESSAGE = "지하철역이 충분하지 않습니다.";

    public InsufficientStationException() {
        super(MESSAGE);
    }
}
