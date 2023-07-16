package nextstep.subway.acceptance;

import io.restassured.response.ValidatableResponse;
import nextstep.subway.controller.resonse.LineResponse;
import nextstep.subway.controller.resonse.StationResponse;
import org.junit.jupiter.api.Assertions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static nextstep.subway.utils.AcceptanceTestUtils.createResource;
import static org.assertj.core.api.Assertions.assertThat;

class SectionAcceptanceTestHelper extends LineAcceptanceTestHelper {

    protected static final String SECTION_RESOURCE_URL = "/sections";


    protected ValidatableResponse createSection(Long lineId, long upStationId, long downStationId, long distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        return createResource(String.format("%s/%d%s", LINES_RESOURCE_URL, lineId, SECTION_RESOURCE_URL), params);
    }

    protected void verifyLineResponse(LineResponse response, String name, String color, long distance) {
        Assertions.assertEquals(name, response.getName());
        Assertions.assertEquals(color, response.getColor());
        Assertions.assertEquals(distance, response.getDistance());
    }

    protected void verifyLineResponseStationNames(LineResponse response, String... stationNames) {
        List<StationResponse> stations = response.getStations();
        assertThat(stations).hasSize(stationNames.length)
                .map(StationResponse::getName)
                .containsExactly(stationNames);
    }
}
