package nextstep.subway.Exception;

public class LineException extends RuntimeException{
    private ErrorCode errorCode;
    private String message;

    public LineException(ErrorCode errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
