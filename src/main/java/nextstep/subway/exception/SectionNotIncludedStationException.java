package nextstep.subway.exception;

public class SectionNotIncludedStationException extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;
    private static final String message = "구간에 포함되지 않은 역입니다.";

    public SectionNotIncludedStationException() {
        super(message);
    }
}