package nextstep.subway.exception;

public class SubwayException extends RuntimeException {

    public SubwayException(SubwayExceptionMessage msg){
        super(msg.getMessage());
    }

}
