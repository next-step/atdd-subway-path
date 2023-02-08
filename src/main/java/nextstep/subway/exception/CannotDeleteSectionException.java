package nextstep.subway.exception;

public class CannotDeleteSectionException extends BadRequestException{
    public CannotDeleteSectionException(String message) {
        super(message);
    }
}
