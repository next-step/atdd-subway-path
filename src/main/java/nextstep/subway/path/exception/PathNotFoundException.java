package nextstep.subway.path.exception;

public class PathNotFoundException extends PathDomainException {
    private static final String MESSAGE = "경로를 찾지 못했습니다.";

    public PathNotFoundException() {
        super(MESSAGE);
    }
}
