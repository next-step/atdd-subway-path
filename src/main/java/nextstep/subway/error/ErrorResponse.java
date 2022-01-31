package nextstep.subway.error;

import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;

import java.util.Collections;
import java.util.List;

public class ErrorResponse {

    private final String defaultMessage;
    private final String detailMessage;
    private final int status;
    private final List<ErrorField> errorFields;
    private final String code;

    static ErrorResponse of(final ErrorCode errorCode) {
        return new ErrorResponse(errorCode);
    }

    static ErrorResponse of(final ErrorCode errorCode, final Errors errors) {
        return new ErrorResponse(errorCode, ErrorField.of(errors));
    }

    static ErrorResponse of(final ErrorCode errorCode, final Errors errors, String detailMessage) {
        return new ErrorResponse(errorCode, ErrorField.of(errors), detailMessage);
    }

    private ErrorResponse(final String defaultMessage, final String detailMessage, final int status, final List<ErrorField> errorFields, final String code) {
        this.defaultMessage = defaultMessage;
        this.detailMessage = detailMessage;
        this.status = status;
        this.errorFields = errorFields;
        this.code = code;
    }

    private ErrorResponse(final ErrorCode errorCode) {
        this(errorCode.getMessage(), StringUtils.EMPTY, errorCode.getStatus().value(), Collections.emptyList(), errorCode.getCode());
    }

    private ErrorResponse(final ErrorCode errorCode, final List<ErrorField> errorFields) {
        this(errorCode.getMessage(), StringUtils.EMPTY, errorCode.getStatus().value(), errorFields, errorCode.getCode());
    }

    private ErrorResponse(final ErrorCode errorCode, final List<ErrorField> errorFields, String detailMessage) {
        this(errorCode.getMessage(), detailMessage, errorCode.getStatus().value(), errorFields, errorCode.getCode());
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    public String getDetailMessage() {
        return detailMessage;
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
