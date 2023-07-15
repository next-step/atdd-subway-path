package nextstep.subway.exception;

import org.springframework.http.HttpStatus;

public class LineDuplicationNameException extends ApiException {

    public static final String message = "지하철 노선 중에 중복된 이름이 있습니다.";

    public LineDuplicationNameException() {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
