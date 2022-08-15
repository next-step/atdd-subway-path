package nextstep.subway.domain;

import nextstep.subway.error.exception.BusinessException;
import nextstep.subway.error.exception.ErrorCode;

class SectionValidator {

    private final static int MINIMUM_SECTION_SIZE = 2;

    static void validateSectionSizeBeforeRemove(Sections sections) {
        if (sections.size() < MINIMUM_SECTION_SIZE) {
            throw new BusinessException(ErrorCode.CANNOT_REMOVE_LAST_SECTION);
        }
    }

    static void validateSectionDistance(Section existsSection, Section newSection) {
        if (existsSection.getDistance() <= newSection.getDistance()) {
            throw new BusinessException(ErrorCode.INVALID_SECTION_DISTANCE);
        }
    }

    static Boolean validateIsStationInSection(Section section, Station station) {
        return section.getUpStation().equals(station) || section.getDownStation().equals(station);
    }
}
