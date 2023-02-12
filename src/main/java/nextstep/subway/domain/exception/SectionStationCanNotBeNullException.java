package nextstep.subway.domain.exception;

public class SectionStationCanNotBeNullException extends IllegalArgumentException {
    private static final String MESSAGE = "구간의 역은 null이 될 수 없습니다.";

    public SectionStationCanNotBeNullException() {
        super(MESSAGE);
    }
}
