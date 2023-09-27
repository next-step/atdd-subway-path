package nextstep.subway.common.exception.line;

import nextstep.subway.common.exception.BusinessException;

public class ContainsAllStationException extends BusinessException {
    public ContainsAllStationException() {
        super("이미 노선에 모든 역이 존재합니다.");
    }
}
