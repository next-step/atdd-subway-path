package nextstep.subway.section.exception;

import nextstep.subway.common.exception.BusinessException;
import nextstep.subway.common.exception.ErrorCode;

public class DuplicatedStationIdException extends BusinessException {
    public DuplicatedStationIdException() {
        super(ErrorCode.DUPLICATED_STATION_ID);
    }
}
