package nextstep.subway.domain.section;

import nextstep.subway.domain.station.Station;
import nextstep.subway.error.exception.BusinessException;
import nextstep.subway.error.exception.ErrorCode;

public class SectionValidator {

    private final static int MINIMUM_SECTION_SIZE = 2;

    public static void validateRemoveSectionIsLastSection(Sections sections, Station station) {
        if (!sections.getLastSection().getDownStation().equals(station)) {
            throw new BusinessException(ErrorCode.CANNOT_REMOVE_SECTION_IF_IS_NOT_DOWN_STATION);
        }
    }

    public static void validateSectionSizeBeforeRemove(Sections sections) {
        if (sections.size() < MINIMUM_SECTION_SIZE) {
            throw new BusinessException(ErrorCode.CANNOT_REMOVE_LAST_SECTION);
        }
    }

    public static void validateSectionDistance(Section existsSection, Section newSection) {
        if (existsSection.getDistance() <= newSection.getDistance()) {
            throw new BusinessException(ErrorCode.INVALID_SECTION_DISTANCE);
        }
    }
}
