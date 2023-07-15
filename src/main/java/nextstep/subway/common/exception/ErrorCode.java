package nextstep.subway.common.exception;

public enum ErrorCode {
    NOT_FOUND(404, "not found value"),
    INVALID_PARAM(403, "invalid parameter"),
    CAN_NOT_REMOVE_STATION(4031, "only one station left"),
    INVALID_INTER_STATION_DISTANCE(4032, "the section distance in inter station should is invalid"),
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
