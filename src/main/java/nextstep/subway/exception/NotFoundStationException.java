package nextstep.subway.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundStationException extends IllegalArgumentException {

    public NotFoundStationException() {
        super("없는 지하철 역입니다.");
    }

}
