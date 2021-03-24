package nextstep.subway.execption;

public class NotExistsStationException extends RuntimeException{
    public NotExistsStationException() {
        super("해당 역이 존재하지 않습니다.");
    }

    public NotExistsStationException(String message) {
        super(message);
    }

    public NotExistsStationException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotExistsStationException(Throwable cause) {
        super(cause);
    }

    protected NotExistsStationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
