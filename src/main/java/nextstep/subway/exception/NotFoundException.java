package nextstep.subway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NotFoundException extends SubwayException {
    public NotFoundException() {
        super("요청한 데이터가 서버에 존재하지 않습니다.");
    }

    public NotFoundException(String message) {
        super(message);
    }
}
