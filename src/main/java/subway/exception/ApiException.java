package subway.exception;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {
    private final ErrorCode errorCode;

    public ApiException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public HttpStatus getStatus() {
        return errorCode.getStatus();
    }

    public String getCode() {
        return errorCode.getCode();
    }
}
