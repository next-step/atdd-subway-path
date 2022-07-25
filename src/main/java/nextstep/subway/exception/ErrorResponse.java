package nextstep.subway.exception;

import org.springframework.http.HttpStatus;

public class ErrorResponse {

    private int status;
    private String message;

    public ErrorResponse() {
    }

    private ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public static ErrorResponse of(HttpStatus status, BusinessException e) {
        return new ErrorResponse(status.value(), e.getMessage());
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }
}
