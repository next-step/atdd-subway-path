package nextstep.subway.domain.exception;

public class InvalidLineNameException extends IllegalArgumentException {

    private static final String MESSAGE = "노선의 이름은 Null 이거나 공란일 수 없습니다.";

    public InvalidLineNameException() {
        super(MESSAGE);
    }
}
