package nextstep.subway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NotValidRequestException extends SubwayException {
    public NotValidRequestException(String message) {
        super(message);
    }
}
