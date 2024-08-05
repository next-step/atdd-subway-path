package nextstep.subway.line.exception;

import nextstep.subway.common.exception.SubwayException;
import nextstep.subway.common.exception.SubwayExceptionType;

public class SectionAlreadyExistsException extends SubwayException {

    public SectionAlreadyExistsException(Long upStationId, Long downStationId) {
        super(SubwayExceptionType.SECTION_ALREADY_EXISTS,
            String.format(SubwayExceptionType.SECTION_ALREADY_EXISTS.getMessage(), upStationId,
                downStationId));
    }
}
