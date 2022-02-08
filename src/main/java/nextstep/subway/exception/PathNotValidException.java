package nextstep.subway.exception;

public class PathNotValidException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private static final String message = "지하철 경로가 존재하지 않습니다.";

    public PathNotValidException() {
        super(message);
    }

    public PathNotValidException(String message) {
        super(message);
    }
}
