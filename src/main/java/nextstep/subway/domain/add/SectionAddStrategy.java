package nextstep.subway.domain.add;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;

public interface SectionAddStrategy {
    boolean match(Sections sections, Section section);

    void add(Sections sections, Section section);
}
