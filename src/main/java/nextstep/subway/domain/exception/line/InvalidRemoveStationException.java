package nextstep.subway.domain.exception.line;

import nextstep.subway.domain.exception.BusinessException;

public class InvalidRemoveStationException extends BusinessException {

    public static final String MESSAGE = LineErrorMessage.INVALID_REMOVE_STATION.getMessage();

    public InvalidRemoveStationException() {
        super(MESSAGE);
    }
}
