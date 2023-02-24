package nextstep.subway.domain.policy;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;

import java.util.List;

public class AddLastSectionPolicy extends AddSectionPolicy {

    private Sections sections;
    private List<Section> sectionList;
    private Section newSection;

    public AddLastSectionPolicy(Sections sections, List<Section> sectionList, Section newSection) {
        this.sections = sections;
        this.sectionList = sectionList;
        this.newSection = newSection;
    }

    @Override
    public void execute() {
        sectionList.add(newSection);
        sections.changeLastStation(newSection.getDownStation());
    }

}
