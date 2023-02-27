package nextstep.subway.domain.policy;

import nextstep.subway.domain.Section;

import java.util.List;

public class AddFirstSectionPolicy extends AddSectionPolicy {

    private List<Section> sectionList;
    private Section newSection;

    public AddFirstSectionPolicy(List<Section> sectionList, Section newSection) {
        this.sectionList = sectionList;
        this.newSection = newSection;
    }

    @Override
    public void execute() {
        sectionList.add(newSection);
    }

}
