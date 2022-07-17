package nextstep.subway.exception;

public class NotFountSectionException extends IllegalArgumentException {
    public NotFountSectionException() {
        super("not found section");
    }
}
