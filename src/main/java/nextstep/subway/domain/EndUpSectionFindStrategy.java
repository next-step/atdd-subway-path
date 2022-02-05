package nextstep.subway.domain;

import java.util.List;

public class EndUpSectionFindStrategy implements SectionFindStrategy {
    private List<Section> sections;

    public EndUpSectionFindStrategy(List<Section> sections) {
        this.sections = sections;
    }

    public Section findToEndUpSection(Section section) {
        for (Section s : sections) {
            if (s.isPrevious(section)) {
                return findToEndUpSection(s);
            }
        }

        return section;
    }

    @Override
    public Section find(Section cursor) {
        return findToEndUpSection(cursor);
    }
}
