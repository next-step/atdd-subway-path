package nextstep.subway.path.exception;

import nextstep.subway.common.exception.BusinessException;
import nextstep.subway.common.exception.ErrorCode;

public class SameSourceAndTargetStationException extends BusinessException {
    public SameSourceAndTargetStationException() {
        super(ErrorCode.SAME_SOURCE_AND_TARGET_STATION);
    }
}
