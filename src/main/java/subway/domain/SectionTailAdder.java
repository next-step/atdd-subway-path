package subway.domain;

import org.springframework.stereotype.Component;

@Component
public class SectionTailAdder implements SectionAdder {
    @Override
    public void apply(SubwayLine subwayLine, SubwaySection subwaySection) {
        if (subwayLine.existsUpStation(subwaySection.getUpStationId()))
            throw new IllegalArgumentException("상행역이 이미 노선에 등록되어 있습니다.");
        subwayLine.registerSection(subwaySection);
    }
}
