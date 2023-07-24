package subway.exception.dto;

import org.springframework.http.HttpStatus;

public class ErrorResponse {

    private HttpStatus httpStatus;
    private String message;

    public ErrorResponse() {

    }

    public ErrorResponse(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}