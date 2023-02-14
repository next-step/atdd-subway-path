package nextstep.subway.domain.exception;

public class IllegalDistanceSectionException extends RuntimeException {

    public IllegalDistanceSectionException() {
        super("잘못된 구간 사이 거리 요청입니다.");
    }

}
