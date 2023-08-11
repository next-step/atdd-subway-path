package subway.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SectionCloseManager {

    SectionCloser getOperator(SubwaySections sections, Station station) {
        if (!sections.existsUpStation(station)) {
            return new SectionTailCloser();
        }

        if (sections.existsDownStation(station)) {
            return new SectionMiddleCloser();
        }
        return new SectionDefaultCloser();
    }

}
