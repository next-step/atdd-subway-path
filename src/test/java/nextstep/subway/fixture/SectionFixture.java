package nextstep.subway.fixture;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

import java.util.HashMap;
import java.util.Map;

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
        return createSection(lineId, upStationId, downStationId, distance, 0);
    }

    public static Section createSection(Long lineId, Long upStationId, Long downStationId, int distance, int orderSeq) {
        return Section.builder()
                .id(lineId)
                .upStation(new Station(upStationId, "역2"))
                .downStation(new Station(downStationId, "역1"))
                .distance(distance)
                .orderSeq(orderSeq)
                .build();
    }

    public static Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId) {
        return createSectionCreateParams(upStationId, downStationId, 6);
    }

    public static Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }
}
