package nextstep.subway.exception;

import org.springframework.http.HttpStatus;

public class StationNotFoundException extends ApiException {

    public static final String message = "해당 지하철 역이 존재하지 않습니다.";

    public StationNotFoundException() {
        super(HttpStatus.NOT_FOUND, message);
    }
}
