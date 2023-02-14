package nextstep.subway.exception;

public class StationNotFoundException extends RuntimeException {

    public static final String MESSAGE = "존재하지 않는 지하철 역입니다. (id: %d)";

    public StationNotFoundException(Long stationId) {
        super(String.format(MESSAGE, stationId));
    }
}
