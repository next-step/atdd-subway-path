package nextstep.subway.fixture;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

public class SectionFixture {

    private SectionFixture() {
    }

    public static Section createSection(Long upStationId, Long downStationId) {
        return createSection(upStationId, downStationId, 10);
    }

    public static Section createSection(Long upStationId, Long downStationId, int distance) {
        return createSection(0L, upStationId, downStationId, distance);
    }

    public static Section createSection(Long lineId, Long upStationId, Long downStationId) {
        return createSection(lineId, upStationId, downStationId, 10);
    }

    public static Section createSection(Long lineId, Long upStationId, Long downStationId, int distance) {
        return Section.builder()
                .id(lineId)
                .upStation(new Station(upStationId, "역2"))
                .downStation(new Station(downStationId, "역1"))
                .distance(distance)
                .build();
    }
}
