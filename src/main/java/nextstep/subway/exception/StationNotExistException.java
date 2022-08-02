package nextstep.subway.exception;

public class StationNotExistException extends BadRequestException {
    public StationNotExistException(String message) {
        super(message);
    }

}
