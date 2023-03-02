package nextstep.subway.domain.policy;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AddSectionPolicies {

    private AddSectionPolicyChain firstPolicy;
    private AddSectionPolicyChain secondPolicy;
    private AddSectionPolicyChain thirdPolicy;

    public AddSectionPolicies() {
        firstPolicy = new AddEmptySectionPolicy();
        secondPolicy = new AddEdgeSectionPolicy();
        thirdPolicy = new AddBetweenSectionPolicy();

        firstPolicy.setNext(secondPolicy);
        secondPolicy.setNext(thirdPolicy);
    }

    public void execute(Sections sections, List<Section> sectionList, Section newSection) {
        firstPolicy.execute(sections, sectionList, newSection);
    }

}
