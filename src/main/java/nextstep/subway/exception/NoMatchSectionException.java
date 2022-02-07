package nextstep.subway.exception;

public class NoMatchSectionException extends RuntimeException {
    private static final String MESSAGE = "상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음";

    public NoMatchSectionException() {
        super(MESSAGE);
    }
}
