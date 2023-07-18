package nextstep.subway.acceptance.steps;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class SectionSteps {

    private static final String BASE_URL = "/lines";


    public static ExtractableResponse<Response> 지하철_노선_구간_제거_요청(Long lineId, Long removeStationId) {
        return RestAssured.given().log().all()
                .when().delete(BASE_URL + "/" + lineId + "/sections?stationId="+removeStationId)
                .then().log().all()
                .extract();

    }


    public static ExtractableResponse<Response> 지하철_노선_구간_등록(Long lineId, Long upStationId, Long downStationId, int distance) {
        Map<String,Object> param = new HashMap<>();
        param.put("downStationId", downStationId);
        param.put("upStationId",upStationId);
        param.put("distance",distance);

        ExtractableResponse<Response> sectionResponse = RestAssured.given().log().all()
                .body(param)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(BASE_URL + "/" + lineId + "/sections")
                .then().log().all()
                .extract();
        return sectionResponse;
    }

}