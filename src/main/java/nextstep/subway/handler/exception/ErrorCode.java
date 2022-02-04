package nextstep.subway.handler.exception;

public enum ErrorCode {
    INVALID_DISTANCE(400, "[ERROR] 거리는 1보다 작을수 없습니다."),

    STATION_NOT_FOUND_BY_ID(404, "[ERROR] 입력한 id의 지하철역을 찾을 수 없습니다."),
    LINE_NOT_FOUND_BY_ID(404, "[ERROR] 입력한 id의 노선을 찾을 수 없습니다."),
    STATIONS_NOT_FOUND_FROM_LINE(404, "[ERROR] 입력한 구간의 두 역을 노선에서 모두 찾을 수 없습니다."),
    NO_CORRECT_SECTION(404, "[ERROR] 해당 역을 갖는 구간은 존재하지 않습니다."),

    STATIONS_ALL_EXISTS(409, "[ERROR] 입력한 구간의 두 역은 이미 노선에 존재합니다."),
    ;

    private final int status;
    private final String detail;

    ErrorCode(int status, String detail) {
        this.status = status;
        this.detail = detail;
    }

    public int getStatus() {
        return status;
    }

    public String getDetail() {
        return detail;
    }
}
