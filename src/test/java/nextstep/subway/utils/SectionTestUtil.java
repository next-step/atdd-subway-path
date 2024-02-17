package nextstep.subway.utils;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class SectionTestUtil {

    /**
     * 구간 등록
     * @param params
     * @param lineId
     * @return
     */
    public static ExtractableResponse<Response> addSection(Map<String, Object> params, long lineId) {
        return RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/{id}/sections", lineId)
                .then().log().all().extract();
    }

    /**
     * 구간 제거
     */
    public static ExtractableResponse<Response> deleteSection(Long lineId, Long stationId) {
        return RestAssured.given().log().all()
                .param("stationId", stationId)
                .when().delete("/lines/{id}/sections", lineId)
                .then().log().all().extract();
    }

    public static Map<String, Object> createSectionParams(Long upStationId, Long downStationId, int distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        return params;
    }
}
