package nextstep.subway.line;


import nextstep.subway.exception.BusinessException;
import nextstep.subway.exception.ErrorMessage;

public class DownstreamStationIncludedException extends BusinessException {

    private static final ErrorMessage ERROR_MESSAGE = ErrorMessage.DOWNSTREAM_STATION_INCLUDED_EXCEPTION;

    public DownstreamStationIncludedException() {
        super(ERROR_MESSAGE.getMessage());
    }
}
