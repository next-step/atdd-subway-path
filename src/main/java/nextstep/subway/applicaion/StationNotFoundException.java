package nextstep.subway.applicaion;

public class StationNotFoundException extends RuntimeException {

    public final static String MESSAGE = "해당 지하철역을 찾을 수 없습니다. (요청 값: %d)";

    public StationNotFoundException(Long stationId) {
        super(String.format(MESSAGE, stationId));
    }
}
