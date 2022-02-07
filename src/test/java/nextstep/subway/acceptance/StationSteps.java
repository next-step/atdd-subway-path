package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static Long 지하철역_식별번호_가져오기(ExtractableResponse<Response> response) {
        return response.jsonPath()
                        .getLong("id");
    }

    public static List<Long> 지하철역_식별번호_내역들_가져오기(ExtractableResponse<Response> response) {
        return response.jsonPath()
                .getList("stations.id", Long.class);
    }
}
