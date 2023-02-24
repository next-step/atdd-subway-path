package nextstep.subway.domain.policy;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;

import java.util.List;

public class AddEmptySectionPolicy extends AddSectionPolicy {

    private Sections sections;
    private List<Section> sectionList;
    private Section newSection;

    public AddEmptySectionPolicy(Sections sections, List<Section> sectionList, Section newSection) {
        this.sections = sections;
        this.sectionList = sectionList;
        this.newSection = newSection;
    }

    @Override
    public void execute() {
        sectionList.add(newSection);
        sections.changeFirstStation(newSection.getUpStation());
        sections.changeLastStation(newSection.getDownStation());
    }

}
