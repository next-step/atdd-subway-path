package nextstep.subway.domain.service;

import java.util.Optional;

import nextstep.subway.domain.model.Line;
import nextstep.subway.domain.model.Section;
import nextstep.subway.domain.model.Sections;

public interface SectionAdditionStrategy {
    String SECTION_NOT_FOUND_TO_ADD_NEW_ONE = "새 구간을 추가할 기존 구간이 존재하지 않습니다.";

    default boolean canApply(Line line, Section newSection) {
        return findExistingSectionForNewAddition(line.getSections(), newSection).isPresent();
    }

    default Optional<Section> findExistingSectionForNewAddition(Sections sections, Section newSection) {
        if (sections.isEmpty()) {
            return Optional.empty();
        }

        for (int i = 0; i < sections.size(); i++) {
            Section currentSection = sections.get(i);
            if (canAddToExistingSection(sections, currentSection, newSection)) {
                return Optional.of(currentSection);
            }
        }

        return Optional.empty();
    }

    default boolean canAddToExistingSection(Sections sections, Section existingSection, Section newSection) {
        return false;
    }

    void addSection(Line line, Section newSection);

    default boolean hasValidDistance(Section existingSection, Section newSection) {
        return existingSection.getDistance() > newSection.getDistance();
    }

    default Integer calculateNewDistance(Section existingSection, Section newSection) {
        return existingSection.getDistance() - newSection.getDistance();
    }

    String getFailureCaseMessage();
}
