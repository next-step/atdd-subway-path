package nextstep.subway.common.exception.station;

import nextstep.subway.common.exception.BusinessException;

public class NotExistStationException extends BusinessException {
    public NotExistStationException() {
        super("역이 존재하지 않습니다.");
    }
}
