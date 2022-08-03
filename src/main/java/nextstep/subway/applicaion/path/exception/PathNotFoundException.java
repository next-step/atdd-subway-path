package nextstep.subway.applicaion.path.exception;

public class PathNotFoundException extends PathException {
    public PathNotFoundException() {
        super("출발역과 도착역 사이의 경로가 없습니다.");
    }
}
