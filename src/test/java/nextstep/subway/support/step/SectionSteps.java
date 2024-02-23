package nextstep.subway.support.step;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Map;
import org.springframework.http.MediaType;

public class SectionSteps {

    public static final String SECTION_BASE_PATH = "/lines/{lineId}/sections";

    public static ExtractableResponse<Response> 지하철_구간_등록_요청(Long lineId, Map<String, Object> body) {
        return RestAssured.given()
            .body(body)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post(SECTION_BASE_PATH, lineId)
            .then()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철_구간_삭제_요청(Long lineId, Long stationId) {
        return RestAssured.given().log().all()
            .queryParam("stationId", stationId)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().delete(SECTION_BASE_PATH, lineId)
            .then().log().all()
            .extract();
    }
}
