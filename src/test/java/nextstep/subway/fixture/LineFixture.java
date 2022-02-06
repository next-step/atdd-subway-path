package nextstep.subway.fixture;

import java.util.Map;

public class LineFixture {

    public static final Map<String, Object> 신분당선 = Map.of(
            "name", "신분당선",
            "color", "bg-red-600",
            "upStationId", 1L,
            "downStationId", 2L,
            "distance", 10
    );
    public static final Map<String, Object> 이호선 = Map.of(
            "name", "이호선",
            "color", "bg-green-600",
            "upStationId", 1L,
            "downStationId", 2L,
            "distance", 10
    );

    public static final Map<String, Object> of(
            String name,
            Long upStationId,
            Long downStationId,
            int distance
    ) {
        return Map.of(
                "name", name,
                "color", "test-color",
                "upStationId", upStationId,
                "downStationId", downStationId,
                "distance", distance
        );
    }
}
