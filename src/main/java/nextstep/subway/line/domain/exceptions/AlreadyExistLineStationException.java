package nextstep.subway.line.domain.exceptions;

public class AlreadyExistLineStationException extends RuntimeException {
    public AlreadyExistLineStationException(String message) {
        super(message);
    }
}
