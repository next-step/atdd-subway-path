package nextstep.subway.line.domain.exception;

public class NoneOfStationEnrolledException extends RuntimeException{
    public NoneOfStationEnrolledException(String message) {
        super(message);
    }
}
