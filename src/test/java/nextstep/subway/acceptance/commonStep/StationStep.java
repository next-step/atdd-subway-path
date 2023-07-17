package nextstep.subway.acceptance.commonStep;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class StationStep {

    public static ExtractableResponse<Response> 지하철역_생성(String stationName) {

        Map<String, String> params = new HashMap<>();
        params.put("name",stationName);

        ExtractableResponse<Response> response =
                RestAssured.given().log().all()
                        .body(params)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().post("/stations")
                        .then().log().all()
                        .extract();

        return response;
    }

    public static JsonPath 지하철역_목록_전체조회() {

        JsonPath result =
                RestAssured.given()
                        .when().get("/stations")
                        .then()
                        .extract().jsonPath();
        return result;
    }
}
