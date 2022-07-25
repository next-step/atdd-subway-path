package nextstep.subway.exception.paths;

import nextstep.subway.exception.BusinessException;

public class EmptyLineException extends BusinessException {

    private static final String EMPTY_LINE_EXCEPTION = "노선이 없으면 그레프를 만들 수 없습니다";

    public EmptyLineException() {
        super(EMPTY_LINE_EXCEPTION);
    }
}
