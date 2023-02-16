package nextstep.subway.applicaion.line.sections.addtional;

import nextstep.subway.domain.section.Section;
import nextstep.subway.domain.line.sections.Sections;

import java.util.List;
import java.util.Optional;

public class SectionFrontAdder implements Additional {
    @Override
    public void add(Sections sections, Section section) {
        sections.isValidationSection(section);

        Optional<Section> opStandardSection = sections.getStandardSectionByDownStation(section);

        if (!opStandardSection.isPresent()) {
            return;
        }

        Section standardSection = opStandardSection.get();

        List<Section> sectionList = sections.getSections();
        sectionList.add(sections.getSectionIndex(standardSection), section);
    }
}
