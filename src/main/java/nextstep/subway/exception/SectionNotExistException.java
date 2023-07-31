package nextstep.subway.exception;

import static nextstep.subway.exception.SubwayError.SECTION_NOT_EXIST_EXCEPTION;

public class SectionNotExistException extends SubwayException {
    public SectionNotExistException() {
        super(SECTION_NOT_EXIST_EXCEPTION);
    }
}
