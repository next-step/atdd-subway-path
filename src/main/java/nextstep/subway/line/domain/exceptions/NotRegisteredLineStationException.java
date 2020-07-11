package nextstep.subway.line.domain.exceptions;

public class NotRegisteredLineStationException extends RuntimeException {
    public NotRegisteredLineStationException(String message) {
        super(message);
    }
}
