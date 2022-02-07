package nextstep.subway.exception;

public enum ErrorMessages {
    DELETE_NOT_AVAILABLE_SECTION("구간이 1개 이하인 경우 역을 삭제할 수 없습니다."),
    SECTION_NOT_FOUND_LAST_DOWN_STATION("구간에 일치하는 하행 종점역이 없습니다."),
    SECTION_NOT_FOUND_FIRST_UP_STATION("상행 종점이 상행역인 구간이 없습니다."),
    DUPLICATE_START_END_STATION("시작역과 종료역이 같습니다."),
    NOT_EXIST_START_END_STATION("존재하지 않은 지하철역입니다.");

    private final String message;

    ErrorMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
