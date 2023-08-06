package subway.domain;

import org.springframework.stereotype.Component;

@Component
public class SectionMiddleCloser implements SectionCloser{
    @Override
    public SubwaySections closeSection(SubwaySections subwaySections, Station station) {
        subwaySections.extendSection(station);
        subwaySections.close(station);
        return subwaySections;
    }
}
