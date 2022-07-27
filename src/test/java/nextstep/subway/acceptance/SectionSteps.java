package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

public class SectionSteps {

    public static ExtractableResponse<Response> 지하철_노선의_구간목록_조회_요청(Long lineId) {
        return RestAssured
                .given().log().all()
                .when().get("/lines/sections/{lineId}", lineId)
                .then().log().all().extract();
    }

    public static Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }
}
