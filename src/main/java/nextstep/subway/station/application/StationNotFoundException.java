package nextstep.subway.station.application;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class StationNotFoundException extends RuntimeException {
    public StationNotFoundException() {
        super("지하철역을 찾을 수 없습니다.");
    }

    public StationNotFoundException(String message) {
        super(message);
    }
}
