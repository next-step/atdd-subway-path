package nextstep.subway.exception;

public class InvalidUpStationException extends RuntimeException {
    public InvalidUpStationException(String message) {
        super(message);
    }
    public InvalidUpStationException(String message, Throwable cause) {
        super(message, cause);
    }
}