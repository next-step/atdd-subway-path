package nextstep.subway.handler.exception;

public enum ErrorCode {
    INVALID_DISTANCE(400, "[ERROR] 거리는 1보다 작을수 없습니다."),
    SECTION_REMAINED_ONLY_ONE(400, "[ERROR] 구간이 1개만 존재하여 삭제할 수 없습니다."),
    TWO_STATIONS_NOT_LINKED(400, "[ERROR] 두 역은 이어져 있지 않아 탐색을 진행할 수 없습니다."),
    TWO_STATIONS_IS_SAME(400, "[ERROR] 두 역은 같아서 탐색이 불가능합니다."),

    STATION_NOT_FOUND_BY_ID(404, "[ERROR] 입력한 id의 지하철역을 찾을 수 없습니다."),
    LINE_NOT_FOUND_BY_ID(404, "[ERROR] 입력한 id의 노선을 찾을 수 없습니다."),
    STATIONS_NOT_FOUND_FROM_LINE(404, "[ERROR] 입력한 구간의 두 역을 노선에서 모두 찾을 수 없습니다."),
    NO_CORRECT_SECTION(404, "[ERROR] 해당 역을 갖는 구간은 존재하지 않습니다."),
    SECTION_NOT_FOUND(404, "[ERROR] 입력한 구간을 찾을 수 없습니다."),
    STATION_NOT_EXISTS_IN_LINE(404, "[ERROR] 입력한 역은 노선에 존재하지 않습니다."),

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
