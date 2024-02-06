package nextstep.subway.Exception;

import org.springframework.http.HttpStatus;

public class ErrorResponse {
    private final ErrorCode errorCode;
    private final int statusCode;
    private final HttpStatus httpStatus;
    private final String message;

    public ErrorResponse(ErrorCode errorCode, String message) {
        this.errorCode = errorCode;
        this.statusCode = errorCode.getHttpStatus().value();
        this.httpStatus = errorCode.getHttpStatus();
        this.message = message;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}
