package nextstep.subway.exception;

public class NotFoundSectionException extends IllegalArgumentException {
    public NotFoundSectionException() {
        super("not found section");
    }
}
