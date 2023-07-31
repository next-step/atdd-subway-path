package nextstep.subway.exception;

import static nextstep.subway.exception.SubwayError.NAME_NOT_AVAILABLE;

public class NameNotAvailableException extends SubwayException {
    public NameNotAvailableException() {
        super(NAME_NOT_AVAILABLE);
    }
}
