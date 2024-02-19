package nextstep.subway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UnconnectedFindPathStationsException extends RuntimeException {

    public UnconnectedFindPathStationsException() {
        super("경로를 조회할 출발역과 도착역이 연결되어 있지 않습니다.");
    }

}

