package nextstep.subway.section.exception;

import nextstep.subway.common.exception.BusinessException;
import nextstep.subway.common.exception.ErrorCode;

public class AlreadyRegisteredStationException extends BusinessException {
    public AlreadyRegisteredStationException() {
        super(ErrorCode.ALREADY_REGISTERED_STATION);
    }
}
