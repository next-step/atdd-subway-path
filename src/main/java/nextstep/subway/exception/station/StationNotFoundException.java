package nextstep.subway.exception.station;

public class StationNotFoundException extends StationException {
    private static String DEFAULT_MESSAGE = "지하철 역을 찾을 수 없습니다.";

    public StationNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public StationNotFoundException(String message) {
        super(message);
    }
}
