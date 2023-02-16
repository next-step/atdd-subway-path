package nextstep.subway.applicaion.line.sections.addtional;

import nextstep.subway.domain.section.Section;
import nextstep.subway.domain.line.sections.Sections;

@FunctionalInterface
public interface Additional {
    void add(Sections sections, Section section);
}
