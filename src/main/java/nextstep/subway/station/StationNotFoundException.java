package nextstep.subway.station;

public class StationNotFoundException extends Exception {
    public StationNotFoundException() {
        super();
    }

    public StationNotFoundException(String message) {
        super(message);
    }
}
