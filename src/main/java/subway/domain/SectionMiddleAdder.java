package subway.domain;

import org.springframework.stereotype.Component;

@Component
public class SectionMiddleAdder implements SectionAdder {
    @Override
    public void execute(SubwayLine subwayLine, SubwaySection subwaySection) {
        subwayLine.reduceSection(subwaySection);
        subwayLine.registerSection(subwaySection);
    }
}
