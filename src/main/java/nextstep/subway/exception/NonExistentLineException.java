package nextstep.subway.exception;

public class NonExistentLineException extends RuntimeException {
    public NonExistentLineException() {
        super("존재하지 않는 지하철 노선입니다.");
    }
}
