package nextstep.subway.error;

class ErrorResponse {

    private final String code;
    private final String desc;

    private ErrorResponse() {
        this.code = null;
        this.desc = null;
    }

    ErrorResponse(final String code, final String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}