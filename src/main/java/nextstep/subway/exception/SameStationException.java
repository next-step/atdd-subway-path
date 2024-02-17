package nextstep.subway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SameStationException extends RuntimeException {

    public SameStationException() {
        super("구간의 상행역과 하앵역이 동일합니다.");
    }

}
