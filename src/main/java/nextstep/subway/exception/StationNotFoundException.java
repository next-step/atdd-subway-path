package nextstep.subway.exception;

public class StationNotFoundException extends RuntimeException{
    private static final String MESSAGE = "지하철역을 찾을 수 없습니다.";

    public StationNotFoundException() {
        super(MESSAGE);
    }
}
