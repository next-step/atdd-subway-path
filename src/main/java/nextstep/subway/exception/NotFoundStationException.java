package nextstep.subway.exception;

public class NotFoundStationException extends IllegalArgumentException {
    public NotFoundStationException() {
        super("not found station");
    }
}
