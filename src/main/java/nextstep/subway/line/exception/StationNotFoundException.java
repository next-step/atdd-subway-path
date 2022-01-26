package nextstep.subway.line.exception;

public class StationNotFoundException extends LineDomainException {
    private static final String MESSAGE = "노선에 존재하지 않은 역입니다.";

    public StationNotFoundException() {
        super(MESSAGE);
    }
}
