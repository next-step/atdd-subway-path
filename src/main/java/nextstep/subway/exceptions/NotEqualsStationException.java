package nextstep.subway.exceptions;

public class NotEqualsStationException extends RuntimeException {
    public static final String DEFAULT_MSG = "해당 지하철역이 동일하지 않습니다.";

    public NotEqualsStationException() {
        super(DEFAULT_MSG);
    }

    public NotEqualsStationException(String message) {
        super(message);
    }
}
