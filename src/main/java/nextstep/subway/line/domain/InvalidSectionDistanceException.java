package nextstep.subway.line.domain;

public class InvalidSectionDistanceException extends RuntimeException{
    public InvalidSectionDistanceException(String msg){
        super(msg);
    }
}
