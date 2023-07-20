package nextstep.subway.section.exception;

import nextstep.subway.common.exception.BusinessException;
import nextstep.subway.common.exception.ErrorCode;

public class DistanceNotLongerThanExistingSectionException extends BusinessException {
    public DistanceNotLongerThanExistingSectionException() {
        super(ErrorCode.DISTANCE_NOT_LONGER_THAN_EXISTING_SECTION);
    }
}
