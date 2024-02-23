package nextstep.subway.path.exception;

public class PathException extends RuntimeException {

    private String message = "잘못된 경로 정보입니다.";

    public PathException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
