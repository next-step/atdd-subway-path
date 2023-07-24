package nextstep.subway.path.exception;

import nextstep.subway.common.exception.BusinessException;
import nextstep.subway.common.exception.ErrorCode;

public class PathNotFoundException extends BusinessException {
    public PathNotFoundException() {
        super(ErrorCode.PATH_NOT_FOUND);
    }
}
