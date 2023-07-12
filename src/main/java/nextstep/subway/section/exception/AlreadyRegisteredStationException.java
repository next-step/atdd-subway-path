package nextstep.subway.section.exception;

import nextstep.subway.common.exception.BusinessException;
import nextstep.subway.common.exception.ErrorCode;

// TODO: 더 적절한 예외명이 없을까? 예외는 상세할수록 좋을 것 같은데.. 메세지로 충분할까?
public class AlreadyRegisteredStationException extends BusinessException {
    public AlreadyRegisteredStationException() {
        super(ErrorCode.ALREADY_REGISTERED_STATION);
    }
}
