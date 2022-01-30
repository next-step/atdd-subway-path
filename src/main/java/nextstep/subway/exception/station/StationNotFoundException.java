package nextstep.subway.exception.station;

public class StationNotFoundException extends RuntimeException {

    private static final String MESSAGE = "역을 찾을 수 없습니다. - %s";

    public StationNotFoundException(Long id) {
        super(String.format(MESSAGE, id));
    }

}
