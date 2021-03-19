package nextstep.subway.line.domain.exception;

public class DomainBusinessException extends RuntimeException{

    public DomainBusinessException(){}
    public DomainBusinessException(String message){
        super(message);
    }
}
