package nextstep.subway.line.exception;

public class DistanceMaximumException extends RuntimeException {
    public DistanceMaximumException() {
        super("새로운 추가 할 역 사이 거리는 기존역 사이 길이보다 같거나 클 수 없습니다.");
    }
}
