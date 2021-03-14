package nextstep.subway.line.domain.exception;

public class BothStationAlreadyEnrolledException extends RuntimeException{
    public BothStationAlreadyEnrolledException(String message) {
        super(message);
    }
}
