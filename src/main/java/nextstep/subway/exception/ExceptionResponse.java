package nextstep.subway.exception;

import lombok.Builder;

@Builder
public class ExceptionResponse {
    private String message;

    public ExceptionResponse() {
    }

    public ExceptionResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
