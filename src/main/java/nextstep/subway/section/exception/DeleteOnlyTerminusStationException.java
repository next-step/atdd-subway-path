package nextstep.subway.section.exception;

import nextstep.subway.common.exception.BusinessException;
import nextstep.subway.common.exception.ErrorCode;

public class DeleteOnlyTerminusStationException extends BusinessException {
    public DeleteOnlyTerminusStationException() {
        super(ErrorCode.DELETE_ONLY_TERMINUS_STATION);
    }
}
