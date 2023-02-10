package nextstep.subway.common;

public enum ErrorMessage {
    ENOUGH_NOT_SECTION_SIZE("지하철 구간은 1개 이상이어야 합니다."),
    ENOUGH_REMOVE_DOWN("하행종점역만 삭제 할 수 있습니다."),
    ENOUGH_ADD_CONNECT("마지막역의 하행종점역이 추가하는 구간의 상행역이여야 추가할 수 있습니다.")
    ;

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }
}
