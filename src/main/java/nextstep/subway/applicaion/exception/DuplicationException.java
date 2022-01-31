package nextstep.subway.applicaion.exception;

import org.springframework.http.HttpStatus;

public class DuplicationException extends BusinessException {

    public static final String MESSAGE = "이미 생성된 개체입니다.";
    private static HttpStatus code = HttpStatus.CONFLICT;

    public DuplicationException() {
        super(MESSAGE, code);
    }

}
