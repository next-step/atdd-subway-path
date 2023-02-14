package nextstep.subway.applicaion.addtional;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;

import java.util.List;
import java.util.Optional;

public class FrontAddSection implements Additional {
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
