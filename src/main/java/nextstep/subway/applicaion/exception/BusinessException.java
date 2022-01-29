package nextstep.subway.applicaion.exception;

import org.springframework.http.HttpStatus;

public class BusinessException extends RuntimeException {
    public static final String MESSAGE = "비즈니스 에러";
    private HttpStatus code = HttpStatus.BAD_REQUEST;

    public BusinessException() {
        super(MESSAGE);
    }

    public BusinessException(String message,HttpStatus code) {
        super(message);
        this.code = code;
    }

    public HttpStatus getCode() {
        return code;
    }
}
