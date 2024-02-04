package nextstep.subway.common.exception;

import org.springframework.http.HttpStatus;

public class ErrorResponse {
    private final String message;
    private final HttpStatus status;

    public ErrorResponse(final String message, final HttpStatus status) {
        this.message = message;
        this.status = status;
    }

    public static ErrorResponse of(final String message, final HttpStatus status) {
        return new ErrorResponse(message, status);
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
