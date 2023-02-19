package nextstep.subway.domain.exception.path;

import nextstep.subway.domain.exception.BusinessException;

public class SourceAndTargetCannotBeSameException extends BusinessException {

    private static final String MESSAGE = PathErrorMessage.SOURCE_AND_TARGET_CANNOT_BE_SAME.getMessage();

    public SourceAndTargetCannotBeSameException() {
        super(MESSAGE);
    }
}
