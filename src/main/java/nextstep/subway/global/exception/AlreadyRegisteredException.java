package nextstep.subway.global.exception;

public class AlreadyRegisteredException extends RuntimeException {

    private static final String MESSAGE = "이미 등록된 상태입니다.";

    public AlreadyRegisteredException() {
        super(MESSAGE);
    }
}
