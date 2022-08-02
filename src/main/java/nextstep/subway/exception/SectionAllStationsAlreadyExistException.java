package nextstep.subway.exception;

public class SectionAllStationsAlreadyExistException extends BadRequestException {

    public SectionAllStationsAlreadyExistException(String message) {
        super(message);
    }

}
