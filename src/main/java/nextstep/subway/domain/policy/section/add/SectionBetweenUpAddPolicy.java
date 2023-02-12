package nextstep.subway.domain.policy.section.add;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.policy.section.SectionAddPolicy;

public class SectionBetweenUpAddPolicy implements SectionAddPolicy {

    @Override
    public boolean isSatisfied(Sections sections, Section section) {
        return sections.isBetweenUp(section);
    }

    @Override
    public void execute(Sections sections, Section section) {

        sections.addBetweenUp(section);
    }
}
