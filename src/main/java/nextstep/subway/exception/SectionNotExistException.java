package nextstep.subway.exception;

public class SectionNotExistException extends BadRequestException {
    public SectionNotExistException(String message) {
        super(message);
    }

}
