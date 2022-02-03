package nextstep.subway.exception;

import nextstep.subway.error.ErrorCode;

public class NotFoundStationException extends NextStepException {

    public NotFoundStationException() {
        super(ErrorCode.NOT_FOUND_STATION_ERROR);
    }
}
