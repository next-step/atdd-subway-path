package nextstep.subway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SameFindPathStationsException extends RuntimeException {

    public SameFindPathStationsException() {
        super("경로를 조회할 출발역과 도착역이 같습니다.");
    }

}
