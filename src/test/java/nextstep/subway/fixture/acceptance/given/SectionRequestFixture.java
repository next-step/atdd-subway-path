package nextstep.subway.fixture.acceptance.given;

import java.util.HashMap;
import java.util.Map;

public abstract class SectionRequestFixture {

    public static final int 구간거리 = 10;

    public static Map<String, Object> 노선구간추가등록_요청데이터(long downStationId, long upStationId,
        int distance) {

        Map<String, Object> params = new HashMap<>();

        params.put("downStationId", downStationId);
        params.put("upStationId", upStationId);
        params.put("distance", distance);

        return params;
    }
}
