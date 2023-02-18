package nextstep.subway.global.error.exception;

import lombok.Getter;

@Getter
public class InvalidValueException extends UserException{

    public InvalidValueException(ErrorCode errorCode) {
        super(errorCode.getStatus(), errorCode.getErrorMessage());
    }
}
