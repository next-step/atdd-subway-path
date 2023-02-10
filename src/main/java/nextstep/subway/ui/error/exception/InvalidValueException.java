package nextstep.subway.ui.error.exception;

public class InvalidValueException extends BusinessException{

    public InvalidValueException(ErrorCode errorCode) {
        super(errorCode);
    }
}