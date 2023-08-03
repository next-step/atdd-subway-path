package nextstep.subway.section.policy.add;

import nextstep.subway.section.repository.Section;
import nextstep.subway.section.repository.Sections;

public interface AddSectionPolicy {
    void validate(Sections sections, Section section);
}
