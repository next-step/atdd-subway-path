package nextstep.subway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AlreadyExistSectionException extends RuntimeException {

    public AlreadyExistSectionException() {
        super("이미 노선에 등록된 구간입니다.");
    }

}
