package nextstep.subway.common.exception;

public class ResourceNotFoundException extends IllegalStateException {
    private static final String MESSAGE = "존재하지 않는 리소스입니다. 요청한 리소스 : %s, %s";

    public ResourceNotFoundException(final Class entityClass, final Long id) {
        super(String.format(MESSAGE, entityClass.getName(), id));
    }

    public ResourceNotFoundException(final Class entityClass, final String message) {
        super(String.format(MESSAGE, entityClass.getName(), message));
    }
}
