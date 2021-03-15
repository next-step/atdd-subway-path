package nextstep.subway.line.domain;

public class InvalidSectionOperationException extends RuntimeException{
    public InvalidSectionOperationException(String msg){
        super(msg);
    }
}