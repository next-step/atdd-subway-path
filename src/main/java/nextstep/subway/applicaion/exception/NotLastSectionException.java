package nextstep.subway.applicaion.exception;

import org.springframework.http.HttpStatus;

public class NotLastSectionException extends BusinessException {

    public static final String MESSAGE = "마지막 역이 아님";
    private static HttpStatus code = HttpStatus.BAD_REQUEST;

    public NotLastSectionException() {
        super(MESSAGE,code);
    }

}
