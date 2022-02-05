package nextstep.subway.domain;

import java.util.List;

public class EndDownSectionFindStrategy implements SectionFindStrategy {
    private List<Section> sections;

    public EndDownSectionFindStrategy(List<Section> sections) {
        this.sections = sections;
    }

    public Section findToEndDownSection(Section section) {
        for (Section s : sections) {
            if (s.isNext(section)) {
                return findToEndDownSection(s);
            }
        }

        return section;
    }

    @Override
    public Section find(Section cursor) {
        return findToEndDownSection(cursor);
    }
}
