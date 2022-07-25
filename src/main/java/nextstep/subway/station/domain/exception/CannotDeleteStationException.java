package nextstep.subway.station.domain.exception;

public class CannotDeleteStationException extends RuntimeException {
    public CannotDeleteStationException(String message) {
        super(message);
    }
}
