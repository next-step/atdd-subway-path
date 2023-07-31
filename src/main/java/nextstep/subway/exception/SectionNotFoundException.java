package nextstep.subway.exception;

import static nextstep.subway.exception.SubwayError.NOT_FOUND_SECTION;

public class SectionNotFoundException extends SubwayException {
    public SectionNotFoundException() {
        super(NOT_FOUND_SECTION);
    }
}
