package nextstep.subway.error.exception;

public class CannotRemoveStateException extends BusinessException {
    public CannotRemoveStateException(Long id) {
        super(makeMessage(id), ErrorCode.CANNOT_REMOVE_STATE);
    }
    private static String makeMessage(Long id) {
        return id + ErrorCode.CANNOT_REMOVE_STATE.getMessage();
    }
}
