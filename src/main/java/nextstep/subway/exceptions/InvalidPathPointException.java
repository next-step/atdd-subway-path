package nextstep.subway.exceptions;

public class InvalidPathPointException extends RuntimeException {
    public static final String DEFAULT_MSG = "경로 검색에 필요한 진입점(혹은 도달점)이 잘못되었습니다.";

    public InvalidPathPointException() {
        super(DEFAULT_MSG);
    }

    public InvalidPathPointException(String message) {
        super(message);
    }
}
