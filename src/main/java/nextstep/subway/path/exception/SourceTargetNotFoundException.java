package nextstep.subway.path.exception;

public class SourceTargetNotFoundException extends PathDomainException {
    private static final String MESSAGE = "존재하지 않는 출발역과 도착역입니다.";

    public SourceTargetNotFoundException() {
        super(MESSAGE);
    }
}
