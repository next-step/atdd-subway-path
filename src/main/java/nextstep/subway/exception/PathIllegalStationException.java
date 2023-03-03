package nextstep.subway.exception;

public class PathIllegalStationException extends RuntimeException {

    private final static String message ="존재하지 않는 역입니다.";
    public PathIllegalStationException() {
        super(message);
    }
}
