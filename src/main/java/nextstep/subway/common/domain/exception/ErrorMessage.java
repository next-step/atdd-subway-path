package nextstep.subway.common.domain.exception;

public enum ErrorMessage {
    NOT_LAST_STATION_DELETED("노선에 포함된 역중 마지막 역만 삭제할 수 있습니다."),
    EXISTS_STATIONS("상행역 또는 하행역이 모두 노선에 이미 존재 합니다."),
    NOT_EXISTS_STATIONS("상행역 또는 하행역이 노선에 존재하지 않습니다."),
    DISTANCE_EXCEEDED("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같을 수 없습니다."),
    NOT_FOUND_REMOVE_SECTION("삭제할 구간을 찾을 수 없습니다."),
    INVALID_SECTION_REMOVE_SIZE("최소 2개 이상의 구간이 존재할 때 삭제할 수 있습니다 ."),
    NOT_FOUND_SECTION("노선에 등록되어 있는 구간을 찾을 수 없습니다."),
    INVALID_SECTION_SIZE("노선에 지하철역은 최소 1개 이상 존재해야 합니다."),
    STATIONS_EXISTS("상행역 또는 하행역이 모두 노선에 이미 존재 합니다."),
    STATIONS_NOT_EXISTS("상행역 또는 하행역이 노선에 존재하지 않습니다."),
    SAME_SOURCE_TARGET("출발역과 도착역이 같을 경우 최단 경로를 조회할 수 없습니다."),
    DISCONNECT_STATIONS("지하철역이 연결되어 있지 않습니다.");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
