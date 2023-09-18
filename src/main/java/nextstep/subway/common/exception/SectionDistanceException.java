package nextstep.subway.common.exception;

public class SectionDistanceException extends BusinessException {
    public SectionDistanceException() {
        super("기존 구간의 거리보다 새로운 구간의 거리가 더 큽니다.");
    }
}
