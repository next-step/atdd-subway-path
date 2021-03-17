package nextstep.subway.line.domain.exception;

public class NotExistedStation extends RuntimeException{

    public NotExistedStation(String message) {
        super(message);
    }
}
