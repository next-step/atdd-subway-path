package subway.domain;

import org.springframework.stereotype.Component;

@Component
public class SectionAddUpdater implements SectionUpdater {
    @Override
    public void apply(SubwayLine subwayLine, SubwaySection subwaySection) {
        subwayLine.addSection(subwaySection);
    }
}
