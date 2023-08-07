package nextstep.subway.exception;

public enum ERROR_CODE {

    NOT_FOUND_ID("존재하지 않는 정보를 입력했습니다."),
    NOT_FOUND_ANY_STATION("상행선과 하행선 둘 중 하나도 포함되어 있지 않은 구간을 추가할 수 없다."),
    DUPLICATED_STATION("상행선과 하행성이 동일하면 등록 할 수 없습니다.");

    private String message;

    ERROR_CODE(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
