package subway.domain;

import org.springframework.stereotype.Component;

@Component
public class SectionDefaultAdder implements SectionAdder {
    @Override
    public SubwaySections addSection(SubwaySections subwaySections, SubwaySection subwaySection) {
        if (subwaySections.existsUpStation(subwaySection.getUpStationId()))
            throw new IllegalArgumentException("상행역이 이미 노선에 등록되어 있습니다.");

        subwaySections.register(subwaySection);
        return subwaySections;
    }
}
