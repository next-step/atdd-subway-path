package nextstep.subway.section.policy.add;

import lombok.RequiredArgsConstructor;
import nextstep.subway.section.repository.Section;
import nextstep.subway.section.repository.Sections;

import java.util.List;

@RequiredArgsConstructor
public class IntegrationAddSectionPolicy implements AddSectionPolicy {
    private final List<AddSectionPolicy> policyList;

    public void validate(Sections sections, Section section) {
        policyList.forEach(addSectionPolicy -> addSectionPolicy.validate(sections, section));
    }
}
