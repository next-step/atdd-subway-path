package nextstep.subway.common;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class SectionRestAssuredCRUD {

    public static ExtractableResponse<Response> addSection(Long upStationId, Long downStationId, int distance, Long lineNum) {

        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("upStationId", upStationId);
        paraMap.put("downStationId", downStationId);
        paraMap.put("distance", distance);

        return RestAssured
                .given().log().all()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .pathParam("id", lineNum)
                    .body(paraMap)
                .when()
                    .post("/lines/{id}/sections")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> deleteSection(Long lineId, Long stationId) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("lineId", lineId)
                .queryParam("stationId", stationId)
                .when()
                .delete("/lines/{lineId}/sections")
                .then().log().all()
                .extract();
    }
}
