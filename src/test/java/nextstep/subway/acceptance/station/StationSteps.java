package nextstep.subway.acceptance.station;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class StationSteps {
    public static ExtractableResponse<Response> 지하철역_생성_요청(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        return RestAssured.given().log().all()
                          .body(params)
                          .contentType(MediaType.APPLICATION_JSON_VALUE)
                          .when()
                          .post("/stations")
                          .then().log().all()
                          .extract();
    }

    public static Long 지하철역_생성_요청_하고_ID_반환(String name) {
        return 지하철역_생성_요청(name).jsonPath().getLong("id");
    }

    public static ExtractableResponse<Response> 지하철역_목록_조회_요청() {
        return RestAssured.given().log().all()
                          .when()
                          .get("/stations/")
                          .then().log().all()
                          .extract();
    }

    public static ExtractableResponse<Response> 지하철역_삭제_요청(Long id) {
        return RestAssured.given().log().all()
                          .when()
                          .delete("/stations/" + id)
                          .then().log().all()
                          .extract();
    }
}
