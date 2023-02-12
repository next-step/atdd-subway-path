package nextstep.subway.domain.policy.section;

import nextstep.subway.domain.Section;

public interface SectionAddPolicy {

    boolean isSatisfied(Section section);
    void execute(Section section);
}
