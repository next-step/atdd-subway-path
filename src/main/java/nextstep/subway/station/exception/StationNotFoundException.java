package nextstep.subway.station.exception;

import nextstep.subway.common.exception.BusinessException;
import nextstep.subway.common.exception.ErrorCode;

public class StationNotFoundException extends BusinessException {
    public StationNotFoundException() {
        super(ErrorCode.STATION_NOT_FOUND);
    }
}
