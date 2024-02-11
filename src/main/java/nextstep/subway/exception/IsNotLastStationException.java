package nextstep.subway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IsNotLastStationException extends IllegalArgumentException {

    public IsNotLastStationException() {
        super("해당 역은 해당 노선에 등록되어있는 하행 종점역이 아닙니다.");
    }

}
