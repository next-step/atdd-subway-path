package nextstep.subway.line.exception;

public class StationDuplicateException extends RuntimeException {
    public StationDuplicateException() {
        super("추가하실 역은 상행역과 하행역이 등록 되어있습니다.");
    }
}
