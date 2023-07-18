package subway.fixture;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.MediaType;

public class SectionFixture {


    /**
     * POST /lines/1/sections
     *
     * @return
     */
    public static ExtractableResponse<Response> createSection(Long lineId, Long upStationId, Long downStationId, Integer distance) {
        Map<String ,String> params = new HashMap<>();
        params.put("upStationId", upStationId.toString());
        params.put("downStationId", downStationId.toString());
        params.put("distance", distance.toString());

        return RestAssured.given().log().all()
            .body(params)
            .pathParam("lineId", lineId)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines/{lineId}/sections")
            .then().log().all()
            .extract();
    }

    /**
     * DELETE /lines/1/sections?stationId=2
     *
     * @return
     */
    public static ExtractableResponse<Response> deleteSection(Long lineId, Long stationId) {

        return RestAssured.given().log().all()
            .pathParam("lineId", lineId)
            .queryParam("stationId", stationId)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().delete("/lines/{lineId}/sections")
            .then().log().all()
            .extract();
    }
}
