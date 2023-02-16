package nextstep.subway.exception;

import org.springframework.http.HttpStatus;

public class StationNotExistException extends SubwayException {
    public static final String MESSAGE = "지하철역이 존재하지 않습니다.";

    public StationNotExistException() {
        super(HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
