package nextstep.subway.exception;

public class AllMatchSectionException extends RuntimeException {
    private static final String MESSAGE = "상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음";

    public AllMatchSectionException() {
        super(MESSAGE);
    }
}
