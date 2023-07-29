package nextstep.subway.exception;

public enum ErrorType {
    STATIONS_NOT_EXIST_IN_LINE("생성하려는 구간의 모든 역이 노선에 존재하지 않습니다."),
    STATIONS_EXIST_IN_LINE("생성하려는 구간의 모든 역이 노선에 이미 존재합니다."),
    SECTION_DISTANCE_TOO_LONG("새로 추가하는 구간의 길이가 기존 구간의 길이보다 큽니다."),
    CANNOT_REMOVE_LAST_SECTION("노선에 구간이 하나만 있는 경우 삭제할 수 없습니다.");

    private final String message;

    ErrorType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return null;
    }
}
