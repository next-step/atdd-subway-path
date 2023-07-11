package nextstep.subway.acceptance;

import io.restassured.response.ValidatableResponse;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.utils.AcceptanceTestUtils.createResource;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

class SectionAcceptanceTestHelper extends LineAcceptanceTestHelper {

    protected static final String SECTION_RESOURCE_URL = "/sections";


    protected ValidatableResponse createSection(Long lineId, long upStationId, long downStationId, long distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        return createResource(String.format("%s/%d%s", LINES_RESOURCE_URL, lineId, SECTION_RESOURCE_URL), params);
    }

    protected void verifySectionAdded(ValidatableResponse createdSectionResponse, String lineName, String color, String upStationName, String downStationName, long distance) {
        createdSectionResponse
                .body("name", equalTo(lineName))
                .body("color", equalTo(color))
                .body("stations", hasSize(2))
                .body("stations[0].name", equalTo(upStationName))
                .body("stations[1].name", equalTo(downStationName))
                .body("distance", equalTo(distance));
    }

}
