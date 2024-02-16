package nextstep.subway.acceptance.fixture;

import java.util.HashMap;
import java.util.Map;

public class SectionFixture {
    public static Map<String, Object> createSectionParams(Long upStationId, Long downStationId, Long distance) {
        Map<String, Object> param = new HashMap<>();
        param.put("upStationId", upStationId);
        param.put("downStationId", downStationId);
        param.put("distance", distance);
        return param;
    }

}
