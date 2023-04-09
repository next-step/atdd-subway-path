package nextstep.subway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AlreadyRegisterBothStationException extends IllegalArgumentException {

    private static final String DUPLICATED_DOWN_STATION = "상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음";

    public AlreadyRegisterBothStationException() {
        super(DUPLICATED_DOWN_STATION);
    }
}
