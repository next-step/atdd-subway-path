package nextstep.subway.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EmptySectionException extends RuntimeException {

    public EmptySectionException() {
        super("해당 노선에 구간이 존재하지 않습니다.");
    }

}
