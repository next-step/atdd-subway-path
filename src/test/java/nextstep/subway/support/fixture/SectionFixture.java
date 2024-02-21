package nextstep.subway.support.fixture;

import java.util.Map;

public class SectionFixture {

    public static Map<String, Object> 구간_생성(Long upStationId, Long downStationId, Long distance) {
        return Map.of(
            "upStationId", upStationId,
            "downStationId", downStationId,
            "distance", distance
        );
    }
}


