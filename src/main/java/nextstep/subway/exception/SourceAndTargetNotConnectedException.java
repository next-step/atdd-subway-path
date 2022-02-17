package nextstep.subway.exception;

public class SourceAndTargetNotConnectedException extends RuntimeException {
    private static final String MESSAGE = "역이 연결되어있지 않습니다.";

    public SourceAndTargetNotConnectedException() {
        super(MESSAGE);
    }
}
