package nextstep.subway.common.domain.model.exception;

public enum ErrorMessage {
    ENTITY_NOT_FOUND("존재하지 않는 %s 입니다."),
    FIELD_DUPLICATE("이미 존재하는 %s 입니다."),

    INVALID_DISTANCE("역과 역사이의 거리는 0보다 길어야 합니다.");


    ErrorMessage(String message) {
        this.message = message;
    }

    private final String message;

    public String getMessage() {
        return message;
    }
}
