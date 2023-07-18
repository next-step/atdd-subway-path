package nextstep.subway.common.exception;

public enum ErrorCode {
    INVALID_SECTION_REGISTRATION(400, "새로운 구간을 등록할 조건을 만족하지 못했습니다. (상행역 또는 하행역이 기존 구간에 존재해야함)"),
    ALREADY_REGISTERED_STATION(400, "같은 구간을 중복하여 등록할 수 없습니다."),
    DELETE_ONLY_TERMINUS_STATION(400, "구간 삭제 시 하행 종점역을 입력해야 합니다."),
    CAN_NOT_DELETE_ONLY_ONE_SECTION(400, "노선에 하나의 구간만 있을 경우 삭제할 수 없습니다."),

    STATION_NOT_FOUND(404, "없는 지하철역입니다."),
    LINE_NOT_FOUND(404, "없는 지하철 노선입니다."),
    ;

    private final int status;
    private final String message;

    ErrorCode(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
