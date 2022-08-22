package nextstep.subway.exception;

public class NotFoundSectionException extends RuntimeException {
    public NotFoundSectionException() {
        super(ErrorMessage.NOT_FOUND_SECTION.getMessage());
    }
}
