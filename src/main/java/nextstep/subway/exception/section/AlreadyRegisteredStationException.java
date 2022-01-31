package nextstep.subway.exception.section;

public class AlreadyRegisteredStationException extends RuntimeException {

    private static final String MESSAGE = "이미 추가된 구간입니다.";

    public AlreadyRegisteredStationException() {
        super(MESSAGE);
    }

}
