package nextstep.subway.utils;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class SectionFactory {
    public static ExtractableResponse<Response> createSection(Long lineId, Long upstationId, Long downstationId) {
        Map<String, String> params = new HashMap<>();
        params.put("upstationId", upstationId.toString());
        params.put("downstationId", downstationId.toString());

        return RestAssured.given().log().all()
                .pathParam("id", lineId)
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/{id}/sections")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> deleteSection(Long lineId, Long stationId) {
        return RestAssured.given().log().all()
                .pathParam("id", lineId)
                .queryParam("stationId", stationId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/lines/{id}/sections")
                .then().log().all()
                .extract();
    }
}
