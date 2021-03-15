package nextstep.subway.line.exception;

public class InvalidSectionDistanceException extends RuntimeException {

    public InvalidSectionDistanceException() {
        super("지하철 구간의 거리가 너무 큽니다.");
    }
}
