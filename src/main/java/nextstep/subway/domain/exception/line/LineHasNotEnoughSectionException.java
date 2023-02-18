package nextstep.subway.domain.exception.line;

import nextstep.subway.domain.exception.BusinessException;

public class LineHasNotEnoughSectionException extends BusinessException {

    public static final String MESSAGE = LineErrorMessage.LINE_HAS_NOT_ENOUGH_SECTION.getMessage();

    public LineHasNotEnoughSectionException() {
        super(MESSAGE);
    }
}
