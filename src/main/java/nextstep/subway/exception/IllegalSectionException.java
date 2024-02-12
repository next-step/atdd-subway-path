package nextstep.subway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class IllegalSectionException extends RuntimeException {
    public IllegalSectionException(String message) {
        super(message);
    }
}
