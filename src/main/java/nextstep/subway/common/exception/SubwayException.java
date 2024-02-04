package nextstep.subway.common.exception;

import org.springframework.http.HttpStatus;

public abstract class SubwayException extends RuntimeException {

    private final HttpStatus status;

    public SubwayException(final HttpStatus status, final String message) {
        super(message);
        this.status = status;
    }

    public SubwayException(final HttpStatus status, final String message, final Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
