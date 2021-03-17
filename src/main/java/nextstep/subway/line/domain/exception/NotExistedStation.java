package nextstep.subway.line.domain.exception;

public class NotExistedStation extends DomainBusinessException{

    public NotExistedStation(String message) {
        super(message);
    }
}
