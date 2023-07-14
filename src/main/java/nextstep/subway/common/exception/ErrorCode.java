package nextstep.subway.common.exception;

public enum ErrorCode {
    NOT_FOUND(404, "not found value"),
    INVALID_PARAM(403, "invalid parameter"),
    CAN_NOT_REMOVE_STATION(4031, "only one station left"),
    ONLY_DOWNWARD_CAN_BE_ADDED_TO_LINE(4032, "downward station not match"),
    ALREADY_IN_LINE(4033, "station already in line")
    ;

    private final Integer code;
    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return this.message;
    }
}
