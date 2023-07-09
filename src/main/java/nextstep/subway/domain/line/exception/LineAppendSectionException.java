package nextstep.subway.domain.line.exception;

import nextstep.subway.global.SubwayException;

public class LineAppendSectionException extends SubwayException {
    private static final String PREFIX = "구간 등록 실패) ";

    public LineAppendSectionException(final String message) {
        super(PREFIX + message);
    }
}
