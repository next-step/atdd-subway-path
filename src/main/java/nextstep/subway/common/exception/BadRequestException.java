package nextstep.subway.common.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends SubwayException {
    public BadRequestException(final String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }

    public BadRequestException(final String message, final Throwable cause) {
        super(HttpStatus.BAD_REQUEST, message, cause);
    }
}
