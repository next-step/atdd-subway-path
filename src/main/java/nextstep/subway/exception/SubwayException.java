package nextstep.subway.exception;

import org.springframework.http.HttpStatus;

public class SubwayException extends RuntimeException {
    private final HttpStatus status;

    public SubwayException(HttpStatus httpStatus, String message) {
        super(message);
        status = httpStatus;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
