package nextstep.subway.exception.path;

public class PathNotFoundException extends PathException {
    private static String DEFAULT_MESSAGE = "경로를 찾을 수 없습니다.";

    public PathNotFoundException() {
    }

    public PathNotFoundException(String message) {
        super(message);
    }
}
