package nextstep.subway.exception;

public class CheckDuplicateStationException extends RuntimeException {
    public CheckDuplicateStationException(String message) {
        super(message);
    }
    public CheckDuplicateStationException(String message, Throwable cause) {
        super(message, cause);
    }

}