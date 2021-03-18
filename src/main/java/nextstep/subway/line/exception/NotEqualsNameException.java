package nextstep.subway.line.exception;

public class NotEqualsNameException extends RuntimeException {
    public NotEqualsNameException() {
        super("상행역 이름이 같지 않습니다.");
    }
}
