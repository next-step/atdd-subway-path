package nextstep.subway.domain.exception;

public class InvalidLineColorException extends IllegalArgumentException {

    private static final String MESSAGE = "색상은 Null 이거나 공란일 수 없습니다.";

    public InvalidLineColorException() {
        super(MESSAGE);
    }
}
