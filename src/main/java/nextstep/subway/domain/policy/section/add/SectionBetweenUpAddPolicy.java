package nextstep.subway.domain.policy.section.add;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.policy.section.SectionAddPolicy;
import nextstep.subway.exception.SubwayException;
import nextstep.subway.exception.SubwayExceptionMessage;

public class SectionBetweenUpAddPolicy implements SectionAddPolicy {

    private final Sections sections;

    public SectionBetweenUpAddPolicy(Sections sections) {
        this.sections = sections;
    }

    @Override
    public boolean isSatisfied(Section section) {
        return sections.isBetweenUp(section);
    }

    @Override
    public void execute(Section section) {
        if (!isSatisfied(section)) {
            throw new SubwayException(SubwayExceptionMessage.SECTION_CANNOT_ADD);
        }
        sections.addBetweenUp(section);
    }
}
