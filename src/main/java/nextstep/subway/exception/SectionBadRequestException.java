package nextstep.subway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SectionBadRequestException extends RuntimeException {

    public SectionBadRequestException() {

    }

    public SectionBadRequestException(String message) {
        super(message);
    }
}
