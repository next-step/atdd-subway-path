package nextstep.subway.exception;

public enum ErrorMessage {
    NOT_FOUND_LINE("조회된 지하철 노선이 없습니다."),
    NOT_FOUND_SECTION("조회된 지하철 구간이 없습니다.")
    ;

    private String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
