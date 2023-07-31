package nextstep.subway.exception;

import static nextstep.subway.exception.SubwayError.SECTION_DISTANCE_OVER_EXCEPTION;

public class SectionDistanceOverException extends SubwayException {
    public SectionDistanceOverException() {
        super(SECTION_DISTANCE_OVER_EXCEPTION);
    }
}
