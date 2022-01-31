package nextstep.subway.common.domain.model.exception;

public class FieldDuplicateException extends RuntimeException {
    public FieldDuplicateException(String fieldName) {
        super(String.format(ErrorMessage.FIELD_DUPLICATE.getMessage(), fieldName));
    }
}
