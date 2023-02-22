package nextstep.subway.exception;

public class PathOriginSameAsTargetException extends SubwayException {

    private static final String MESSAGE ="경로의 출발지와 도착지는 같을 수 없습니다.";

    public PathOriginSameAsTargetException() {
        super(MESSAGE);
    }
}
