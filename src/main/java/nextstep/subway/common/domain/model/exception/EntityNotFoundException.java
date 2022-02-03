package nextstep.subway.common.domain.model.exception;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String entityName) {
        super(String.format(ErrorMessage.ENTITY_NOT_FOUND.getMessage(), entityName));
    }
}
