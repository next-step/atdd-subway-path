package nextstep.subway.domain;

import java.util.List;
import java.util.Optional;

public class EndDownSectionFindStrategy implements SectionFindStrategy {
    private List<Section> sections;

    public EndDownSectionFindStrategy(List<Section> sections) {
        this.sections = sections;
    }

    public Section findToEndDownSection(Section section) {
        Optional<Section> sectionOptional = sections.stream()
                .filter(section::isPrevious)
                .findFirst();
        return sectionOptional.isPresent() ? findToEndDownSection(sectionOptional.get()) : section;
    }

    @Override
    public Section find(Section cursor) {
        return findToEndDownSection(cursor);
    }
}
