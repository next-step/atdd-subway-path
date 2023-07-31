package nextstep.subway.exception;

import static nextstep.subway.exception.SubwayError.NOT_FOUND_LINE;

public class LineNotFoundException extends SubwayException {
    public LineNotFoundException() {
        super(NOT_FOUND_LINE);
    }
}
