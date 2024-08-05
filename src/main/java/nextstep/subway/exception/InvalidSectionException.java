package nextstep.subway.exception;

import org.springframework.http.HttpStatus;

public class InvalidSectionException extends SubwayException {
    public InvalidSectionException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
