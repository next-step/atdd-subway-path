package nextstep.subway.exception;

public class NotFoundSectionException extends RuntimeException {
    public NotFoundSectionException() {
        super("not found section");
    }
}
