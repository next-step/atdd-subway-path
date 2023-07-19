package nextstep.subway.acceptance;

import io.restassured.path.json.JsonPath;
import io.restassured.response.ValidatableResponse;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.utils.AcceptanceTestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

class LineAcceptanceTestHelper extends StationAcceptanceTestHelper {

    protected static final String LINES_RESOURCE_URL = "/lines";
    protected static final String UP_STATION_ID_JSON_PATH = "stations[0].id";
    protected static final String DOWN_STATION_ID_JSON_PATH = "stations[1].id";

    protected ValidatableResponse createLines(String lineName, String color, String upStationName, String downStationName, long distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", lineName);
        params.put("color", color);
        params.put("upStationId", getId(createStation(upStationName)));
        params.put("downStationId", getId(createStation(downStationName)));
        params.put("distance", distance);

        return createResource(LINES_RESOURCE_URL, params);
    }

    protected void verifyFoundLine(ValidatableResponse foundLineResponse, String lineName, String color, long distance, String... names) {
        JsonPath jsonPath = foundLineResponse.extract().jsonPath();

        assertThat(jsonPath.getString("name")).isEqualTo(lineName);
        assertThat(jsonPath.getString("color")).isEqualTo(color);
        assertThat(jsonPath.getLong("distance")).isEqualTo(distance);
        assertThat(jsonPath.getList("stations.name", String.class)).containsExactly(names);
    }

    protected void verifyFoundLineWithPath(String path, ValidatableResponse foundLineResponse, String lineName, String color, String... names) {
        JsonPath jsonPath = foundLineResponse.extract().jsonPath();

        assertThat(jsonPath.getString(path + ".name")).isEqualTo(lineName);
        assertThat(jsonPath.getString(path + ".color")).isEqualTo(color);
        assertThat(jsonPath.getList(path + ".stations.name", String.class)).containsExactly(names);
    }


    protected ValidatableResponse modifyLine(String lineName, String color, String url) {
        Map<String, String> params = new HashMap<>();
        params.put("name", lineName);
        params.put("color", color);

        return modifyResource(url, params);
    }
}
