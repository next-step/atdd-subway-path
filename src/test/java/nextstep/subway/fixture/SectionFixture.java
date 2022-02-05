package nextstep.subway.fixture;

import java.util.Map;

public class SectionFixture {

    public static Map<String, Object> of(Long upStationId, Long downStationId, int distance) {
        return Map.of(
                "downStationId", downStationId,
                "upStationId", upStationId,
                "distance", distance
        );
    }

}
