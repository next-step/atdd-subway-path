package nextstep.subway.handler.exception;

public class SectionException extends RuntimeException {
    private final ErrorCode errorCode;

    public SectionException(ErrorCode errorCode) {
        super(errorCode.getDetail());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
