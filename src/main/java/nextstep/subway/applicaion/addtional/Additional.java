package nextstep.subway.applicaion.addtional;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;

@FunctionalInterface
public interface Additional {
    void add(Sections sections, Section section);
}
