package nextstep.subway.line.exception;

public class NotExistPathInfoException extends RuntimeException {
    private static final String NOT_EXIST_PATH_INFO = "출발역과 도착역 경로 정보가 없습니다.";

    public NotExistPathInfoException() {
        super(NOT_EXIST_PATH_INFO);
    }
}
