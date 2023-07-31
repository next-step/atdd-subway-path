package nextstep.subway.exception;

import static nextstep.subway.exception.SubwayError.SECTION_EXIST_EXCEPTION;

public class SectionExistException extends SubwayException {
    public SectionExistException() {
        super(SECTION_EXIST_EXCEPTION);
    }
}
