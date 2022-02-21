package nextstep.subway.error;

import nextstep.subway.error.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public class ErrorResponse {

    private final String message;
    private final HttpStatus status;

    public ErrorResponse(ErrorCode code) {
        this.message = code.getMessage();
        this.status = code.getStatus();
    }

    public HttpStatus getStatus() {
        return status;
    }
}
