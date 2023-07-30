package nextstep.subway.exception;

import static nextstep.subway.exception.SubwayError.SECTION_DELETE_EXCEPTION;

public class SectionDeleteException extends SubwayException {
    public SectionDeleteException() {
        super(SECTION_DELETE_EXCEPTION);
    }
}
