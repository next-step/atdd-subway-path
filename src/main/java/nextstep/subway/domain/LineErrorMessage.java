package nextstep.subway.domain;

public enum LineErrorMessage {
    ADD_SECTION_INVALID_DISTANCE("역 사이에 기존 역 사이 길이보다 크거나 같은 구간을 등록할 수 없습니다."),
    ADD_SECTION_STATIONS_ALREADY_EXIST("새로운 구간의 상행역과 하행역이 이미 모두 노선에 등록되어 있습니다."),
    ADD_SECTION_STATIONS_NOT_EXIST("새로운 구간의 상행역과 하행역이 모두 노선에 등록되어 있지 않습니다."),

    REMOVE_SECTION_LAST_ONE("노선의 마지막 하나 남은 구간은 삭제할 수 없습니다."),

    REMOVE_SECTION_STATIONS_NOT_EXISTS("노선에 등록되어 있지 않은 역은 제거할 수 없습니다."),
    ;

    private final String message;

    LineErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
