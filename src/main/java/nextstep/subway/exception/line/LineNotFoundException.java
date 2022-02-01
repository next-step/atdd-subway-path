package nextstep.subway.exception.line;

import nextstep.subway.exception.ServiceException;

public class LineNotFoundException extends ServiceException {

    private static final String MESSAGE = "노선을 찾을 수 없습니다.";

    public LineNotFoundException() {
        super(MESSAGE);
    }

}
