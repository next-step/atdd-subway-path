package nextstep.subway.station.exception;

public class StationNotFoundException extends RuntimeException {
    public StationNotFoundException() {
        super();
    }

    public StationNotFoundException(String message) {
        super(message);
    }

    public StationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public StationNotFoundException(Throwable cause) {
        super(cause);
    }
}
