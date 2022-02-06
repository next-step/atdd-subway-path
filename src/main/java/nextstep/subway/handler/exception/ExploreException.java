package nextstep.subway.handler.exception;

public class ExploreException extends RuntimeException{
    private final ErrorCode errorCode;

    public ExploreException(ErrorCode errorCode) {
        super(errorCode.getDetail());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
