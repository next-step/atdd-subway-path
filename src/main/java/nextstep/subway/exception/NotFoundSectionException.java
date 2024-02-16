package nextstep.subway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundSectionException extends RuntimeException {

    public NotFoundSectionException() {
        super("없는 지하철 구간입니다.");
    }

}

