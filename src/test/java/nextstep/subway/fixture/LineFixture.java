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
            "upStationId", 2L,
            "downStationId", 3L,
            "distance", 10
    );
}
