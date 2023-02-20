package nextstep.subway.common;

public enum ErrorMessage {
    ENOUGH_NOT_SECTION_SIZE("지하철 구간은 1개 이상이어야 합니다."),
    ENOUGH_REMOVE_DOWN("하행종점역만 삭제 할 수 있습니다."),
    ENOUGH_ADD_CONNECT("마지막역의 하행종점역이 추가하는 구간의 상행역이여야 추가할 수 있습니다."),
    INVALID_SECTION_STATE("저장된 구간정보가 정상적이지 않습니다."),
    DUPLICATED_STATION("상행역과 하행역이 이미 노선에 모두 등록되어 있습니다."),
    NOT_CONNECT_STATION("상행역 또는 하행역과 연결되지 않은 지하철은 등록할 수 없습니다."),
    NOT_FOUND_UP_STATION("존재하지 않는 상행역입니다."),
    NOT_FOUND_DOWN_STATION("존재하지 않는 하행역입니다."),
    INVALID_DISTANCE("지하철 구간의 간격이 잘못되었습니다."),
    DUPLICATED_PATH_FIND("출발역과 도착역이 같습니다."),
    NOT_CONNECT_PATH("출발역과 도착역이 연결되어 있지 않습니다."),
    NOT_FOUND_SOURCE("존재하지 않은 출발역 입니다."),
    NOT_FOUND_TARGET("존재하지 않은 도착역 입니다."),
    ;

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }
}
