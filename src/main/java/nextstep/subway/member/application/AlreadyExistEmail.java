package nextstep.subway.member.application;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class AlreadyExistEmail extends RuntimeException {
    public AlreadyExistEmail(String message) {
        super(message);
    }
}
