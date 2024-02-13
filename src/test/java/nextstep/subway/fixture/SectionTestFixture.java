package nextstep.subway.fixture;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.HashMap;

public class SectionTestFixture {

    public static ExtractableResponse<Response> createSection(long upStationId, long downStationId, int distance, long lineId) {
        return RestAssured
                .given()
                .contentType("application/json")
                .body(new HashMap<String, String>() {{
                    put("upStationId", String.valueOf(upStationId));
                    put("downStationId", String.valueOf(downStationId));
                    put("distance", String.valueOf(distance));
                }})
                .when().post("/lines/{id}/sections", lineId)
                .then().log().all().extract();
    }
}
