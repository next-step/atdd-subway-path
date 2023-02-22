package nextstep.subway.exception;

public class StationNotFoundException extends SubwayException {

    private static final String MESSAGE ="지하철 역을 찾을 수 없습니다.";

    public StationNotFoundException() {
        super(MESSAGE);
    }
}
