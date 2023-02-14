package nextstep.subway.applicaion.addtional;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;

import java.util.List;

public class BackAddSection implements Additional {
    @Override
    public void add(Sections sections, Section section) {
        sections.isValidationSection(section);

        List<Section> sectionList = sections.getSections();

        sectionList.add(section);
    }
}
