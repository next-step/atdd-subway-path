package nextstep.subway.applicaion.strategy;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;

@AllArgsConstructor
public class SectionAdditor {
    private Sections sections;
    private AdditionalStrategy additionalStrategy;

    public void add(Section section) {
        if(isChangeUpTerminus(section)) {
            sections.add(section);
            return;
        }

        if (canAddToMiddleSection(section)) {
            additionalStrategy.add(section);
            return;
        }

        sections.add(section);
    }

    private boolean canAddToMiddleSection(Section section) {
        return sections.getValues().stream()
                .anyMatch(
                        origin -> ( origin.isEqualToDownStation(section.getDownStation())
                                || origin.isEqualToUpStation(section.getUpStation()) )
                        );
    }

    private boolean isChangeUpTerminus(Section section) {
        return section.getDownStation().equals(sections.findUpTerminus());
    }
}
