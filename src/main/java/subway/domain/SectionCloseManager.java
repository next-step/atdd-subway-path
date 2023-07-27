package subway.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SectionCloseManager {
    private final SectionTailCloser sectionTailCloser;

    @Autowired
    public SectionCloseManager(SectionTailCloser sectionTailCloser) {
        this.sectionTailCloser = sectionTailCloser;
    }

    SectionCloser getOperator(SubwayLine subwayLine) {
        return sectionTailCloser;
    }

}
