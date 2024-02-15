package nextstep.subway.global.exception;

public class SectionMismatchException extends RuntimeException {

    private static final String MESSAGE = "구간의 상행역과 노선의 하행역이 일치하지 않습니다.";

    public SectionMismatchException() {
        super(MESSAGE);
    }
}
