package nextstep.subway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DuplicatedElementException extends RuntimeException {
    public DuplicatedElementException() {
        super();
    }

    public DuplicatedElementException(String message) {
        super(message);
    }
}
