package nextstep.subway.error.exception;

public class InvalidValueException extends BusinessException{
    public InvalidValueException() {
        super(ErrorCode.INVALID_INPUT_VALUE);
    }
    public InvalidValueException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
    public InvalidValueException(ErrorCode errorCode) {
        super(errorCode);
    }
    public InvalidValueException(Long id) {
        super(makeMessage(id), ErrorCode.INVALID_INPUT_VALUE);
    }
    private static String makeMessage(Long id) {
        return id + ErrorCode.INVALID_INPUT_VALUE.getMessage();
    }
}
