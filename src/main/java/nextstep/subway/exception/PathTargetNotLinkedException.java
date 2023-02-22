package nextstep.subway.exception;

public class PathTargetNotLinkedException extends SubwayException {

    private static final String MESSAGE ="도착지 역은 출발지 역과 연결되어 있어야 합니다.";

    public PathTargetNotLinkedException() {
        super(MESSAGE);
    }
}
