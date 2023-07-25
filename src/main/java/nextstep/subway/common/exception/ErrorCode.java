package nextstep.subway.common.exception;

public enum ErrorCode {
    INVALID_SECTION_REGISTRATION(400, "새로운 구간을 등록할 조건을 만족하지 못했습니다. (상행역 또는 하행역이 기존 구간에 존재해야함)"),
    ALREADY_REGISTERED_STATION(400, "같은 구간을 중복하여 등록할 수 없습니다."),
    DISTANCE_NOT_LONGER_THAN_EXISTING_SECTION(400, "역 사이에 새로운 구간을 등록할 때, 거리가 기존 구간보다 짧아야 합니다."),
    DELETE_ONLY_TERMINUS_STATION(400, "구간 삭제 시 하행 종점역을 입력해야 합니다."),
    CAN_NOT_DELETE_ONLY_ONE_SECTION(400, "노선에 하나의 구간만 있을 경우 삭제할 수 없습니다."),
    DUPLICATED_STATION_ID(400, "상행 종점역과 하행 종점역의 Id는 서로 같을 수 없습니다."),
    SAME_SOURCE_AND_TARGET_STATION(400, "경로검색 시 출발역과 도착역은 같을 수 없습니다."),
    STATION_NOT_IN_GIVEN_LINES(400, "경로검색 시 노선상에 존재하는 지하철 역만 검색할 수 있습니다."),

    STATION_NOT_FOUND(404, "없는 지하철역입니다."),
    LINE_NOT_FOUND(404, "없는 지하철 노선입니다."),
    SECTION_NOT_FOUND(404, "없는 지하철 구간입니다."),
    PATH_NOT_FOUND(404, "지하철 경로를 탐색하지 못했습니다. (출발역과 도착역이 연결되어 있는지 확인)"),;

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
