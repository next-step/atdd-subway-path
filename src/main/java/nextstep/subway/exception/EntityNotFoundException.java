package nextstep.subway.exception;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(Long id, String message) {
        super(String.format("%s %s does not exist.", message, id));
    }

    public EntityNotFoundException(String message) {
        super(String.format("%s does not exist.", message));
    }
}
