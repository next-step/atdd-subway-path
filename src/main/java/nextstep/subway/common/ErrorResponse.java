package nextstep.subway.common;

public class ErrorResponse {

    private final String code;
    private final String message;

    private ErrorResponse() {
        this.code = null;
        this.message = null;
    }

    ErrorResponse(final String code, final String desc) {
        this.code = code;
        this.message = desc;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
