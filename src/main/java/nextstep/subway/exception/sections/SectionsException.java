package nextstep.subway.exception.sections;

import nextstep.subway.exception.BusinessException;
import nextstep.subway.exception.ErrorCode;

public class SectionsException extends BusinessException {

    public SectionsException(ErrorCode errorCode) {
        super(errorCode);
    }
}
