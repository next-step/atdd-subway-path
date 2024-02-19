package nextstep.subway.domain.exception;

public class PathException extends DomainException {

    private static final String PATH_NOT_FOUND_EXCEPTION = "경로를 찾을 수 없습니다.";
    private static final String PATH_SOURCE_TARGET_SAME_EXCEPTION = "출발역과 도착역이 같습니다.";
    private static final String SOURCE_TARGET_NOT_CONNECTED_EXCEPTION = "출발역과 도착역이 연결되어 있지 않습니다.";

    public PathException(String message) {
        super(message);
    }

    public static class PathNotFoundException extends PathException {

        public PathNotFoundException() {
            super(PATH_NOT_FOUND_EXCEPTION);
        }
    }

    public static class PathSourceTargetSameException extends PathException {

        public PathSourceTargetSameException() {
            super(PATH_SOURCE_TARGET_SAME_EXCEPTION);
        }
    }

    public static class SourceTargetNotConnectedException extends PathException {

        public SourceTargetNotConnectedException() {
            super(SOURCE_TARGET_NOT_CONNECTED_EXCEPTION);
        }

    }
}
