package nextstep.subway.line.exception;

import nextstep.subway.common.exception.BusinessException;
import nextstep.subway.common.exception.ErrorCode;

public class LineNotFoundException extends BusinessException {
    public LineNotFoundException() {
        super(ErrorCode.LINE_NOT_FOUND);
    }
}
