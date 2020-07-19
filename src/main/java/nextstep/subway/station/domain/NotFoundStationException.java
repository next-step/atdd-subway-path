package nextstep.subway.station.domain;

public class NotFoundStationException extends RuntimeException {
    public NotFoundStationException() {
        super("not found station");
    }
}
