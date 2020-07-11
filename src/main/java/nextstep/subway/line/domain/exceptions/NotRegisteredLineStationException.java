package nextstep.subway.line.domain.exceptions;

public class NotRegisteredLineStationException extends RuntimeException {
    public NotRegisteredLineStationException() {
        super("not registered line station");
    }
}
