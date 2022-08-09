package nextstep.subway.exception;

public class NonExistentSectionException extends RuntimeException {
    public NonExistentSectionException() {
        super("존재하지 않는 지하철 구간입니다.");
    }
}
