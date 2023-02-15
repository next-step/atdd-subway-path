package nextstep.subway.exception;

public class EmptySectionException extends BadRequestException {
    public EmptySectionException(String message) {
        super(message);
    }
}
