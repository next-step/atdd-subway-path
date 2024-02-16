package nextstep.subway.exception;

public class InvalidDownStationException extends RuntimeException{
    public InvalidDownStationException(String message) {
        super(message);
    }

    public InvalidDownStationException(String message, Throwable cause) {
        super(message, cause);
    }
}