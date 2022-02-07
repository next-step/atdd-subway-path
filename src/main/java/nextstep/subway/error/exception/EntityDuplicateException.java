package nextstep.subway.error.exception;

public class EntityDuplicateException extends InvalidValueException{
    public EntityDuplicateException() {
        super(ErrorCode.ENTITY_DUPLICATION);
    }
    public EntityDuplicateException(String name) {
        super(makeMessage(name), ErrorCode.ENTITY_DUPLICATION);
    }
    private static String makeMessage(String name) {
        return name + ErrorCode.ENTITY_DUPLICATION.getMessage();
    }
}
