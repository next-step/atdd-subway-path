package nextstep.subway.line.exception;

public class NoSuchLineException extends RuntimeException{
    public NoSuchLineException(String msg){
        super(msg);
    }
}
