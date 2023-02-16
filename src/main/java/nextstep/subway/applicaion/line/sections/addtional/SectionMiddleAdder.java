package nextstep.subway.applicaion.line.sections.addtional;

import nextstep.subway.domain.section.Distance;
import nextstep.subway.domain.section.Section;
import nextstep.subway.domain.line.sections.Sections;

import java.util.List;
import java.util.Optional;

public class SectionMiddleAdder implements Additional {
    @Override
    public void add(Sections sections, Section section) {
        sections.isValidationSection(section);

        Optional<Section> opStandardSection = sections.getStandardSectionByUpStation(section);

        if (!opStandardSection.isPresent()) {
            return;
        }
        Section standardSection = opStandardSection.get();

        int standardIndex = sections.getSectionIndex(standardSection);

        Section newSection = Section.builder()
                .line(section.getLine())
                .upStation(section.getDownStation())
                .downStation(standardSection.getDownStation())
                .distance(new Distance(standardSection.minusDistance(section.getDistance())))
                .build();

        standardSection.change(standardSection.getUpStation(), section.getDownStation(), new Distance(section.getDistance()));

        List<Section> sectionList = sections.getSections();

        sectionList.set(standardIndex, standardSection);
        sectionList.add(standardIndex+1, newSection);
    }
}
