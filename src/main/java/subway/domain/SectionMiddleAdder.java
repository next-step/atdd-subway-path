package subway.domain;

import org.springframework.stereotype.Component;

@Component
public class SectionMiddleAdder implements SectionAdder {
    @Override
    public SubwaySections addSection(SubwaySections subwaySections, SubwaySection subwaySection) {
        subwaySections.reduceSection(subwaySection);
        subwaySections.register(subwaySection);
        return subwaySections;
    }
}
