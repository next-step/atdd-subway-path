package subway.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SectionAddManager {
    private final SectionTailAdder sectionAddOperator;
    private final SectionMiddleAdder sectionMiddleAdder;
    private final SectionHeadAdder sectionHeadAdder;

    SectionAdder getUpdater(SubwayLine subwayLine, SubwaySection subwaySection) {
        if (subwayLine.isStartStation(subwaySection.getDownStationId())) {
            return sectionHeadAdder;
        }
        if (subwayLine.hasDuplicateSection(subwaySection)) {
            return sectionMiddleAdder;
        }
        return sectionAddOperator;
    }
}
