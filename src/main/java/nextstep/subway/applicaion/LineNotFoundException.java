package nextstep.subway.applicaion;

public class LineNotFoundException extends RuntimeException {

    public LineNotFoundException(String message) {
        super(message);
    }
}
