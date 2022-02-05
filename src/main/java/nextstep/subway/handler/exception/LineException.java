package nextstep.subway.handler.exception;

public class LineException extends RuntimeException {
    private final ErrorCode errorCode;

    public LineException(ErrorCode errorCode) {
        super(errorCode.getDetail());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
