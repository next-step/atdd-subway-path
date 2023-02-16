package nextstep.subway.applicaion.line.sections.addtional;

import nextstep.subway.domain.section.Section;
import nextstep.subway.domain.line.sections.Sections;

import java.util.List;

public class SectionAdder implements Additional {
    @Override
    public void add(Sections sections, Section section) {
        sections.isValidationSection(section);

        List<Section> sectionList = sections.getSections();

        sectionList.add(section);
    }
}
