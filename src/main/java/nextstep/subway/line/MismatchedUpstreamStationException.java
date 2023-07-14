package nextstep.subway.line;


import nextstep.subway.exception.BusinessException;
import nextstep.subway.exception.ErrorMessage;

public class MismatchedUpstreamStationException extends BusinessException {

    private static final ErrorMessage ERROR_MESSAGE = ErrorMessage.MISMATCHED_UPSTREAM_STATION_EXCEPTION;

    public MismatchedUpstreamStationException() {
        super(ERROR_MESSAGE.getMessage());
    }
}
