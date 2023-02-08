package nextstep.subway.exception;

public class LineNotFoundException extends NotFoundException{
    public LineNotFoundException(long id) {
        super("line not found : " + id);
    }
}
