package nextstep.subway.applicaion.exception;

import org.springframework.http.HttpStatus;

public class IllegalSectionException extends BusinessException {

    public static final String MESSAGE = "제약을 준수하세요";
    private static HttpStatus code = HttpStatus.BAD_REQUEST;

    public IllegalSectionException() {
        super(MESSAGE, code);
    }
}
