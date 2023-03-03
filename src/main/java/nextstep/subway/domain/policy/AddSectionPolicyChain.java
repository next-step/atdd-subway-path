package nextstep.subway.domain.policy;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;

import java.util.List;

public abstract class AddSectionPolicyChain {

    private AddSectionPolicyChain next;

    public void setNext(AddSectionPolicyChain next) {
        this.next = next;
    }

    public void execute(Sections sections, List<Section> sectionList, Section newSection) {
        if (supported(sections, newSection)) {
            addSection(sectionList, newSection);
            return;
        }

        if (next == null) {
            return;
        }

        next.execute(sections, sectionList, newSection);
    }

    abstract protected boolean supported(Sections sections, Section newSection);
    abstract protected void addSection(List<Section> sectionList, Section newSection);

}
