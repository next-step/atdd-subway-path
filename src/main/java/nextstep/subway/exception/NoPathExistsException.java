package nextstep.subway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NoPathExistsException extends SubwayException {
    public NoPathExistsException() {
        super("해당 역간에 존재하는 경로가 없습니다.");
    }

    public NoPathExistsException(String message) {
        super(message);
    }
}
