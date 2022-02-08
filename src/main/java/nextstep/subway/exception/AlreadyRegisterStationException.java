package nextstep.subway.exception;

public class AlreadyRegisterStationException extends RuntimeException {
    private static final String MESSAGE = "등록하려는 구간의 역들이 모두 등록되어 있습니다.";

    public AlreadyRegisterStationException() {
        super(MESSAGE);
    }
}
