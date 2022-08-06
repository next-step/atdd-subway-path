package nextstep.subway.exception;

public class NoPathException extends RuntimeException {

    private static final String NO_PATH_EXCEPTION = "입력받은 출발역에서 도착역으로 이동가능한 경로가 없습니다.";

    public NoPathException() {
        super(NO_PATH_EXCEPTION);
    }

}