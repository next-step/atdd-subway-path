package nextstep.subway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static nextstep.subway.exception.Messages.ILLEGAL_ADD_SECTION;

public class IllegalAddSectionException extends RuntimeException {
    public IllegalAddSectionException() {
        super(ILLEGAL_ADD_SECTION.message());
    }
}
