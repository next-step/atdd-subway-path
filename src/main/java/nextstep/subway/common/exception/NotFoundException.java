package nextstep.subway.common.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends SubwayException {
    public NotFoundException(final String message) {
        super(HttpStatus.NOT_FOUND, message);
    }

    public NotFoundException(final String message, final Throwable cause) {
        super(HttpStatus.NOT_FOUND, message, cause);
    }
}
