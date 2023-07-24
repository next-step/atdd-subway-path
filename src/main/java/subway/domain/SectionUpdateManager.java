package subway.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SectionUpdateManager {
    private final SectionAddUpdater sectionAddOperator;

    @Autowired
    public SectionUpdateManager(SectionAddUpdater sectionAddOperator) {
        this.sectionAddOperator = sectionAddOperator;
    }

    SectionUpdater getUpdater(SubwayLine subwayLine) {
        return sectionAddOperator;
    }
}
