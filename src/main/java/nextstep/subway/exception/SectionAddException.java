package nextstep.subway.exception;

import static nextstep.subway.exception.SubwayError.SECTION_ADD_EXCEPTION;

public class SectionAddException extends SubwayException {
    public SectionAddException() {
        super(SECTION_ADD_EXCEPTION);
    }
}
