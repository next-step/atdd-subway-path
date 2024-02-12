package nextstep.subway.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundLineException extends IllegalArgumentException {

    public NotFoundLineException() {
        super("없는 지하철 노선입니다.");
    }

}
