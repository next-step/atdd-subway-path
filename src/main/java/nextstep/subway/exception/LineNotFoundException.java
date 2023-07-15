package nextstep.subway.exception;

import org.springframework.http.HttpStatus;

public class LineNotFoundException extends ApiException {

    public static final String message = "해당 지하철 노선이 존재하지 않습니다.";

    public LineNotFoundException() {
        super(HttpStatus.NOT_FOUND, message);
    }
}
