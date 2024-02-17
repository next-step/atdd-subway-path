package nextstep.subway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NotPositiveDistanceException extends RuntimeException {

    public NotPositiveDistanceException() {
        super("구간의 길이는 0보다 커야합니다.");
    }

}
