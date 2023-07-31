package nextstep.subway.domain.add;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;

public class AddMiddleByDownStation implements SectionAddStrategy {
    @Override
    public boolean match(Sections sections, Section section) {
        return sections.containsAtDownStation(section.getDownStation());
    }

    @Override
    public void add(Sections sections, Section section) {
        Section existSection = sections.findIncluded(section.getDownStation()).get(0);
        existSection.updateUpStation(section);
        int index = sections.indexOf(existSection);
        sections.add(index + 1, section);
    }
}
