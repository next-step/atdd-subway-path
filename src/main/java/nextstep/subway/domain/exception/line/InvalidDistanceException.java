package nextstep.subway.domain.exception.line;

import nextstep.subway.domain.exception.BusinessException;

public class InvalidDistanceException extends BusinessException {


    private static final String MESSAGE = LineErrorMessage.INVALID_DISTANCE.getMessage();

    public InvalidDistanceException() {
        super(MESSAGE);
    }
}
