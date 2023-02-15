package nextstep.subway.domain.policy.section;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;

public interface SectionAddPolicy {

    boolean isSatisfied(Sections sections, Section section);

    void execute(Sections sections, Section section);
}
