package nextstep.subway.exception;

public class InvaliadDistanceException extends RuntimeException {
    public InvaliadDistanceException() {
        super("기존에 등록된 구간의 거리를 초과합니다.");
    }
}
