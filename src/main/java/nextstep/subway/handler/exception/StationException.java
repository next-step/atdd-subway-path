package nextstep.subway.handler.exception;

public class StationException extends RuntimeException {
    private final ErrorCode errorCode;

    public StationException(ErrorCode errorCode) {
        super(errorCode.getDetail());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
