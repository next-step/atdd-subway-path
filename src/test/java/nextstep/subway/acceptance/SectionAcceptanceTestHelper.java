package nextstep.subway.acceptance;

import io.restassured.response.ValidatableResponse;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.utils.AcceptanceTestUtils.createResource;

class SectionAcceptanceTestHelper extends LineAcceptanceTestHelper {

    protected static final String SECTION_RESOURCE_URL = "/sections";


    protected ValidatableResponse createSection(Long lineId, long upStationId, long downStationId, long distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        return createResource(String.format("%s/%d%s", LINES_RESOURCE_URL, lineId, SECTION_RESOURCE_URL), params);
    }
}
