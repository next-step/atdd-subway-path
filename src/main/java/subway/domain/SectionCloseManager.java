package subway.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SectionCloseManager {
    private final SectionDefaultCloser sectionDefaultCloser;
    private final SectionTailCloser sectionTailCloser;
    private final SectionMiddleCloser sectionMiddleCloser;


    SectionCloser getOperator(SubwaySections sections, Station station) {
        if (!sections.existsUpStation(station)) {
            return sectionTailCloser;
        }

        if (sections.existsDownStation(station)) {
            return sectionMiddleCloser;
        }
        return sectionDefaultCloser;
    }

}
