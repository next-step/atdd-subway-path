package nextstep.subway.common.exception.line;

import nextstep.subway.common.exception.BusinessException;

public class OnlyOneSectionException extends BusinessException {
    public OnlyOneSectionException() {
        super("노선에 역이 1개만 존재합니다.");
    }
}
