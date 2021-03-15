package nextstep.subway.line.domain;

public class InvalidStationException extends RuntimeException{
    public InvalidStationException(String msg){
        super(msg);
    }
}
