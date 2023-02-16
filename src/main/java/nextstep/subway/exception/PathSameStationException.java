package nextstep.subway.exception;

import org.springframework.http.HttpStatus;

public class PathSameStationException extends SubwayException {
    public static final String MESSAGE = "출발역과 도착역이 같습니다.";

    public PathSameStationException() {
        super(HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
