package nextstep.subway.member.application;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class AlreadyExistEmailException extends RuntimeException {
    public AlreadyExistEmailException() {
        super("이미 존재하는 이메일 입니다.");
    }
}
