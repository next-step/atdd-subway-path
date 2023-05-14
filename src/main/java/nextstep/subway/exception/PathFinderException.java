package nextstep.subway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PathFinderException extends RuntimeException {

    public PathFinderException() {
    }

    public PathFinderException(String message) {
        super(message);
    }
}
