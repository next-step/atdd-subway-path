package nextstep.subway.line.exception;

public class HaveOnlyOneSectionException extends RuntimeException {

    public static final String MESSAGE = "구간이 한 개만 있는 경우 삭제가 불가능합니다.";

    public HaveOnlyOneSectionException() {
        super(MESSAGE);
    }
}
