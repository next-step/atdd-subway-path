package nextstep.subway.line.application;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class LineNotFoundException extends RuntimeException {
    public LineNotFoundException() {
        super("지하철 노선을 찾을 수 없습니다.");
    }

    public LineNotFoundException(String message) {
        super(message);
    }
}
