package nextstep.subway.global.exception;

public class StationNotMatchException extends RuntimeException {

    private static final String MESSAGE = "지하철 역이 일치하지 않습니다.";

    public StationNotMatchException() {
        super(MESSAGE);
    }
}
