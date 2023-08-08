package subway.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SectionAddManager {
    private final SectionDefaultAdder sectionDefaultOperator;
    private final SectionMiddleAdder sectionMiddleAdder;

    SectionAdder getUpdater(SubwaySections sections, SubwaySection newSection) {
        if (sections.hasDuplicateSection(newSection)) {
            return sectionMiddleAdder;
        }
        return sectionDefaultOperator;
    }
}
