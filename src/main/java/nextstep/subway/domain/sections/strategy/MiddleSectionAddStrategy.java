package nextstep.subway.domain.sections.strategy;

import static java.util.stream.Collectors.*;

import java.util.List;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.exception.CannotAddSectionException;
import nextstep.subway.domain.sections.Sections;

public class MiddleSectionAddStrategy implements SectionAddStrategy {
    private static final String LONGER_DISTANCE_EXCEPTION_MESSAGE = "새로운 구간의 길이는 본 구간의 길이보다 짧아야 합니다.";

    @Override
    public boolean meetCondition(Sections sections, Section newSection) {
        List<Section> matchedSections = getMatchedSections(sections, newSection);

        if (matchedSections.isEmpty()) {
            return false;
        }

        int sectionDistance = matchedSections.get(0).getDistance();
        int newSectionDistance = newSection.getDistance();

        if (sectionDistance <= newSectionDistance) {
            throw new CannotAddSectionException(LONGER_DISTANCE_EXCEPTION_MESSAGE);
        }

        return true;
    }

    @Override
    public ChangeableSections findChangeableSections(Sections sections, Section newSection, Line line) {
        List<Section> matchedSections = getMatchedSections(sections, newSection);

        Section deprecatedSection = matchedSections.get(0);
        int sectionDistance = matchedSections.get(0).getDistance();
        int newSectionDistance = newSection.getDistance();

        Section subsequentSection = new Section(line, newSection.getDownStation(), deprecatedSection.getDownStation(), sectionDistance - newSectionDistance);

        return new ChangeableSections(List.of(subsequentSection), List.of(deprecatedSection));
    }

    private List<Section> getMatchedSections(Sections sections, Section newSection) {
        return sections.getValue().stream()
            .filter(section -> section.isSameUpStation(newSection.getUpStation().getId()))
            .collect(toList());
    }
}
