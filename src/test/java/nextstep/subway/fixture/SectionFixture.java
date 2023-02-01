package nextstep.subway.fixture;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

public class SectionFixture {

    private SectionFixture() {
    }

    public static Section createSection(Long upStationId, Long downStationId) {
        return Section.builder()
                .id(0L)
                .upStation(new Station(upStationId, "역2"))
                .downStation(new Station(downStationId, "역1"))
                .distance(10)
                .build();
    }

    public static Section createSection(Long lineId, Long upStationId, Long downStationId) {
        return Section.builder()
                .id(lineId)
                .upStation(new Station(upStationId, "역2"))
                .downStation(new Station(downStationId, "역1"))
                .distance(10)
                .build();
    }
}
