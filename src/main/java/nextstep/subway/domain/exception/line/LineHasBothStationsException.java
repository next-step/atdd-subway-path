package nextstep.subway.domain.exception.line;

import nextstep.subway.domain.exception.BusinessException;

public class LineHasBothStationsException extends BusinessException {

    private static final String MESSAGE = LineErrorMessage.LINE_HAS_BOTH_STATION.getMessage();

    public LineHasBothStationsException() {
        super(MESSAGE);
    }
}
