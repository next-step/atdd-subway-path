package nextstep.subway.error;

import org.springframework.validation.Errors;

import java.util.Collections;
import java.util.List;

public class ErrorResponse {

    private final String message;
    private final int status;
    private final List<ErrorField> errorFields;
    private final String code;

    static ErrorResponse of(final ErrorCode errorCode) {
        return new ErrorResponse(errorCode);
    }

    static ErrorResponse of(final ErrorCode errorCode, final Errors errors) {
        return new ErrorResponse(errorCode, ErrorField.of(errors));
    }

    private ErrorResponse(final String message, final int status, final List<ErrorField> errorFields, final String code) {
        this.message = message;
        this.status = status;
        this.errorFields = errorFields;
        this.code = code;
    }

    private ErrorResponse(final ErrorCode errorCode) {
        this(errorCode.getMessage(), errorCode.getStatus().value(), Collections.emptyList(), errorCode.getCode());
    }

    private ErrorResponse(final ErrorCode errorCode, final List<ErrorField> errorFields) {
        this(errorCode.getMessage(), errorCode.getStatus().value(), errorFields, errorCode.getCode());
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public List<ErrorField> getErrorFields() {
        return errorFields;
    }

    public String getCode() {
        return code;
    }
}
