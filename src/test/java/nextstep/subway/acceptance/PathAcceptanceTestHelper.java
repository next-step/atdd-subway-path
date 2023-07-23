package nextstep.subway.acceptance;

import io.restassured.path.json.JsonPath;
import io.restassured.response.ValidatableResponse;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.utils.AcceptanceTestUtils.getResource;
import static org.assertj.core.api.Assertions.assertThat;

class PathAcceptanceTestHelper extends SectionAcceptanceTestHelper {

    protected static final String PATH_RESOURCE_URL = "/paths";

    protected ValidatableResponse getPath(Long sourceStationId, Long targetStationId) {
        Map<String, Object> params = new HashMap<>();
        params.put("sourceId", sourceStationId);
        params.put("targetId", targetStationId);

        return getResource(PATH_RESOURCE_URL, params);
    }

    protected void verifyFoundPath(ValidatableResponse foundPathResponse, long distance, String... stationNames) {
        JsonPath jsonPath = foundPathResponse.extract().jsonPath();
        assertThat(jsonPath.getList("stations.name", String.class)).containsExactly(stationNames);
        assertThat(jsonPath.getLong("distance")).isEqualTo(distance);
    }
}
