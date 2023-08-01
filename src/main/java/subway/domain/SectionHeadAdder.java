package subway.domain;

import org.springframework.stereotype.Component;

@Component
public class SectionHeadAdder implements SectionAdder {
    @Override
    public void execute(SubwayLine subwayLine, SubwaySection subwaySection) {
        if (!subwayLine.isStartStation(subwaySection.getDownStationId())) {
            throw new IllegalArgumentException("하행역이 노선의 기점이 아닙니다.");
        }
        subwayLine.updateStartStation(subwaySection.getUpStationId());
        subwayLine.registerSection(subwaySection);
    }
}
