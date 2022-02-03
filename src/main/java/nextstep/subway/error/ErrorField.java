package nextstep.subway.error;

import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ErrorField {

    private final String field;
    private final String value;
    private final String reason;

    static List<ErrorField> of(final Errors errors) {
        if(errors == null) {
            return Collections.emptyList();
        }
        return errors.getFieldErrors()
                .stream()
                .map(fieldError -> new ErrorField(
                        fieldError.getField(),
                        getOrEmpty(fieldError.getRejectedValue()),
                        getOrEmpty(fieldError.getDefaultMessage())
                ))
                .collect(Collectors.toList());
    }

    private ErrorField(String field, String value, String reason) {
        this.field = field;
        this.value = value;
        this.reason = reason;
    }

    private static String getOrEmpty(final Object target) {
        if (Objects.isNull(target)) {
            return StringUtils.EMPTY;
        }
        return target.toString();
    }
}
