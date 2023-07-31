package nextstep.subway.domain.add;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;

public class AddMiddleByUpStation implements SectionAddStrategy {
    @Override
    public boolean match(Sections sections, Section section) {
        return sections.containsAtUpStation(section.getUpStation());
    }

    @Override
    public void add(Sections sections, Section section) {
        Section existSection = sections.findIncluded(section.getUpStation()).get(0);
        existSection.updateDownStation(section);
        int index = sections.indexOf(existSection);
        sections.add(index, section);
    }
}
