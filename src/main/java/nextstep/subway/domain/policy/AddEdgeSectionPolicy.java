package nextstep.subway.domain.policy;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;

import java.util.List;

public class AddEdgeSectionPolicy extends AddSectionPolicyChain {

    @Override
    public boolean supported(Sections sections, Section newSection) {
        return sections.isNewFirstSection(newSection)
                || sections.isNewLastSection(newSection);
    }

    @Override
    protected void addSection(List<Section> sectionList, Section newSection) {
        sectionList.add(newSection);
    }

}
