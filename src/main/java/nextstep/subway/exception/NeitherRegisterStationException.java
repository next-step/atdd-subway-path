package nextstep.subway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NeitherRegisterStationException extends IllegalArgumentException {

    private static final String DUPLICATED_DOWN_STATION = "상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음";

    public NeitherRegisterStationException() {
        super(DUPLICATED_DOWN_STATION);
    }
}
