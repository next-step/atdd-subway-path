package nextstep.subway.error.exception;

public class EntityNotFoundException extends BusinessException{
    public EntityNotFoundException() {
        super(ErrorCode.ENTITY_NOT_FOUND);
    }
    public EntityNotFoundException(Long id) {
        super(makeMessage(id), ErrorCode.ENTITY_NOT_FOUND);
    }
    private static String makeMessage(Long id) {
        return id + ErrorCode.ENTITY_NOT_FOUND.getMessage();
    }
}
