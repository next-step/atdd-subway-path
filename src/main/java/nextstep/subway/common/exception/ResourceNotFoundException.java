package nextstep.subway.common.exception;

public class ResourceNotFoundException extends IllegalStateException {

    public ResourceNotFoundException(final Class entityClass, final Long id) {
        super(String.format("존재하지 않는 리소스입니다. 요청한 리소스 : %s, id : %d", entityClass.getName(), id));
    }

    public ResourceNotFoundException(final Class entityClass, final String message) {
        super(String.format("존재하지 않는 리소스입니다. 요청한 리소스 : %s, %s", entityClass.getName(), message));
    }
}
