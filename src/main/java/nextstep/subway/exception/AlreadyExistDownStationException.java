package nextstep.subway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AlreadyExistDownStationException extends RuntimeException {

    public AlreadyExistDownStationException() {
        super("새로운 구간의 상행역이 해당 노선에 등록되어있는 하행 종점역이 아닙니다.");
    }

}
