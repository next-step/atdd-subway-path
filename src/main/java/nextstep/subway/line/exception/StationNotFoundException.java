package nextstep.subway.line.exception;

public class StationNotFoundException extends LineDomainException {
    private final static String MESSAGE = "상행역과 하행역 둘 중 하나도 포함되어 있지 않습니다.";

    public StationNotFoundException() {
        super(MESSAGE);
    }
}
