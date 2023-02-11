package nextstep.subway.domain.exception;

public class StationNotInSectionsException extends IllegalArgumentException {
    private static final String MESSAGE = "구간 목록에 포함되지 않은 역입니다.";

    public StationNotInSectionsException() {
        super(MESSAGE);
    }
}
