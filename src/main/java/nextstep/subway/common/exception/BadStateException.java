package nextstep.subway.common.exception;

import org.springframework.http.HttpStatus;

public class BadStateException extends SubwayException {
    public BadStateException(final String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }

    public BadStateException(final String message, final Throwable cause) {
        super(HttpStatus.BAD_REQUEST, message, cause);
    }
}
