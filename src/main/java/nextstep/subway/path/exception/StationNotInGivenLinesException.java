package nextstep.subway.path.exception;

import nextstep.subway.common.exception.BusinessException;
import nextstep.subway.common.exception.ErrorCode;

public class StationNotInGivenLinesException extends BusinessException {
    public StationNotInGivenLinesException() {
        super(ErrorCode.STATION_NOT_IN_GIVEN_LINES);
    }
}
