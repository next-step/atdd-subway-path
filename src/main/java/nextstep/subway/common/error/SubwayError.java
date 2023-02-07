package nextstep.subway.common.error;

public enum SubwayError {
    NO_FIND_LINE("노선이 존재하지 않습니다."),
    NO_REGISTER_EXIST_STATION("요청한 상행역과 하행역이 이미 노선에 등록되어 있어서 추가가 불가능합니다."),
    NO_REGISTER_NO_EXIST_STATION("요청한 상행역과 하행역 모두 노선에 등록되어 있지 않아서 추가가 불가능합니다."),
    NO_DELETE_ONE_SECTION("노선의 구간 목록수가 1개인 경우 삭제할 수 없습니다."),
    NO_LAST_SECTION("노선의 구간이 존재하지 않아서 불가능합니다."),
    NO_REGISTER_LAST_LINE_STATION("요청한 역으로 등록된 마지막 구간이 존재하지 않습니다."),
    NO_REGISTER_DISTANCE_GREATER_THAN("요청한 구간의 길이는 기존 구간보다 크거나 같으면 추가가 불가능합니다."),
    ;

    private final String message;

    SubwayError(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
