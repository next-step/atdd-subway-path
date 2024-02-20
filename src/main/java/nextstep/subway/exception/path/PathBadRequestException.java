package nextstep.subway.exception.path;

public class PathBadRequestException extends PathException {
    private static String DEFAULT_MESSAGE = "잘못된 경로 요청입니다.";

    public PathBadRequestException() {
        super(DEFAULT_MESSAGE);
    }

    public PathBadRequestException(String message) {
        super(message);
    }
}
