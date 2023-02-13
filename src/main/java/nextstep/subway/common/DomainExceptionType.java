package nextstep.subway.common;

public enum DomainExceptionType {
    UNKNOWN_RULE("정의되지 않은 Domain 규칙입니다"),
    NO_LINE("존재하지 않는 노선입니다."),
    NO_STATION("존재하지 않는 역입니다."),
    UPDOWN_STATION_MISS_MATCH("노선의 상행역과 추가하려는 구간의 하행역이 일치하지 않습니다"),
    DOWN_STATION_EXIST_IN_LINE("추가하려는 구간 하행역이 이미 노선에 존재합니다."),
    NOT_DOWN_STATION("노선의 하행역이 포함된 구간만 제거할 수 있습니다"),
    CANT_DELETE_SECTION("노선에 구간이 1개일 경우 구간 제거가 불가합니다.");

    private final String defaultMessage;

    DomainExceptionType(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    public String getCode() {
        return this.toString();
    }

    public String getMessage() {
        return defaultMessage;
    }
}
