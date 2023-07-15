package nextstep.subway.common.exception;

public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public Integer getCode() {
        return errorCode.getCode();
    }
    public String getMessage() {
        return errorCode.getMessage();
    }
}
