package subway.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SectionUpdateManager {
    private final SectionTailAdder sectionAddOperator;

    @Autowired
    public SectionUpdateManager(SectionTailAdder sectionAddOperator) {
        this.sectionAddOperator = sectionAddOperator;
    }

    SectionAdder getUpdater(SubwayLine subwayLine) {
        return sectionAddOperator;
    }
}
