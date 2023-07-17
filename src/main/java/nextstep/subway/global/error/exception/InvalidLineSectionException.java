package nextstep.subway.global.error.exception;

import nextstep.subway.global.error.code.ErrorCode;
import org.springframework.http.HttpStatus;

public class InvalidLineSectionException extends BusinessException {

    public InvalidLineSectionException(ErrorCode errorCode) {
        super(HttpStatus.BAD_REQUEST.value(), errorCode.getMessage());
    }

}
