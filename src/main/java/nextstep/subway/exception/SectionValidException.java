package nextstep.subway.exception;

public class SectionValidException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private static final String message = "상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없습니다.";

    public SectionValidException() {
        super(message);
    }
}
