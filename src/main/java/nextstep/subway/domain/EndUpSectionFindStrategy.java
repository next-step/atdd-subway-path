package nextstep.subway.domain;

import java.util.List;
import java.util.Optional;

public class EndUpSectionFindStrategy implements SectionFindStrategy {
    private List<Section> sections;

    public EndUpSectionFindStrategy(List<Section> sections) {
        this.sections = sections;
    }

    public Section findToEndUpSection(Section section) {
        Optional<Section> sectionOptional = sections.stream()
                .filter(section::isNext)
                .findFirst();
        return sectionOptional.isPresent() ? findToEndUpSection(sectionOptional.get()) : section;
    }

    @Override
    public Section find(Section cursor) {
        return findToEndUpSection(cursor);
    }
}
