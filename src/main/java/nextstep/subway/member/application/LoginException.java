package nextstep.subway.member.application;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class LoginException extends RuntimeException {
    public LoginException(String message) {
        super(message);
    }
}
