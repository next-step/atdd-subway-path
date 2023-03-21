package nextstep.subway.common.exception;


import org.springframework.http.HttpStatus;

public class SubwayException extends RuntimeException{
    private final ErrorCode errorCode;
    private final HttpStatus httpStatus;
    private final String message;


    public SubwayException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.httpStatus = errorCode.getHttpStatus();
        this.message = errorCode.getMessage();
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
