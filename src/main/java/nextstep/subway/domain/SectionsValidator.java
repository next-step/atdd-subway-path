package nextstep.subway.domain;

import java.util.List;
import nextstep.subway.domain.exception.AddSectionConstraintException;
import nextstep.subway.domain.exception.AddSectionDistanceOverExistingSection;

public class SectionsValidator {

    public void addSection(
            final List<Section> sections,
            final List<Section> sectionsBasedOnUpStation,
            final List<Section> sectionsBasedOnDownStation) {
        if (isAlreadyAddedSection(sectionsBasedOnUpStation, sectionsBasedOnDownStation)
                || isNonIncludeStation(sections, sectionsBasedOnUpStation, sectionsBasedOnDownStation)) {
            throw new AddSectionConstraintException();
        }
    }

    private boolean isAlreadyAddedSection(
            final List<Section> upStationSection,
            final List<Section> downStationSection
    ) {
        return !upStationSection.isEmpty() && !downStationSection.isEmpty();
    }

    private boolean isNonIncludeStation(
            final List<Section> sections,
            final List<Section> upStationSection,
            final List<Section> downStationSection
    ) {
        return !sections.isEmpty() && upStationSection.isEmpty() && downStationSection.isEmpty();
    }

    public void distanceAddSectionBetweenExistingSection(
            final Section newSection,
            final Section existingSection
    ) {
        if (newSection.getDistance().more(existingSection.getDistance())) {
            throw new AddSectionDistanceOverExistingSection();
        }
    }
}
