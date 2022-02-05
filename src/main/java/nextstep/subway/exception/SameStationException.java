package nextstep.subway.exception;

import nextstep.subway.error.ErrorCode;

public class SameStationException extends NextStepException {

    public SameStationException() {
        super(ErrorCode.SAME_STATION_ERROR);
    }
}
