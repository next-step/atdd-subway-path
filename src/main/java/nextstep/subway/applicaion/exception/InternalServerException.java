package nextstep.subway.applicaion.exception;

import org.springframework.http.HttpStatus;

public class InternalServerException extends ApplicationException {
    public InternalServerException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}
