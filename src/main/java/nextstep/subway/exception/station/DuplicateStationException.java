package nextstep.subway.exception.station;

public class DuplicateStationException extends RuntimeException {

    private static final String MESSAGE = "역 이름이 중복 됩니다. - %s";

    public DuplicateStationException(String stationName) {
        super(String.format(MESSAGE, stationName));
    }

}
