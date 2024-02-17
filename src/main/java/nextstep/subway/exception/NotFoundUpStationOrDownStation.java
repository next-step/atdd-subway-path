package nextstep.subway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NotFoundUpStationOrDownStation extends RuntimeException {

    public NotFoundUpStationOrDownStation() {
        super("상행역 혹은 하행역을 찾을 수 없습니다.");
    }

}
