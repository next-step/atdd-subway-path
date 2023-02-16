package nextstep.subway.exception;

import org.springframework.http.HttpStatus;

public class PathNotFoundException extends SubwayException {
    public static final String MESSAGE = "경로가 연결되어이 있지 않습니다.";

    public PathNotFoundException() {
        super(HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
