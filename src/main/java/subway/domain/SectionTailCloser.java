package subway.domain;

import org.springframework.stereotype.Component;

@Component
public class SectionTailCloser implements SectionCloser{
    @Override
    public void apply(SubwayLine subwayLine, Station station) {
        subwayLine.closeSection(station);
    }
}
