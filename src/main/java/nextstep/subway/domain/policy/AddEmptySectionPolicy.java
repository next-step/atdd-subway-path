package nextstep.subway.domain.policy;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;

import java.util.List;

public class AddEmptySectionPolicy extends AddSectionPolicyChain {

    @Override
    protected boolean supported(Sections sections, Section newSection) {
        return sections.isEmpty();
    }

    @Override
    protected void addSection(List<Section> sectionList, Section newSection) {
        sectionList.add(newSection);
    }

}
