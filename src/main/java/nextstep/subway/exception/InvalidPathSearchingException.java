package nextstep.subway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidPathSearchingException extends RuntimeException {
    public InvalidPathSearchingException() {
        super();
    }

    public InvalidPathSearchingException(String message) {
        super(message);
    }
}
