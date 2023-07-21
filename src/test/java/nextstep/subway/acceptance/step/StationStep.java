package nextstep.subway.acceptance.step;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Map;
import org.springframework.http.MediaType;

public class StationStep {
    private StationStep() {
    }

    public static ExtractableResponse<Response> 지하철역을_생성한다(String stationName) {
        Map<String, String> params = Map.of("name", stationName);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철역_목록을_조회한다() {
        return RestAssured.given().log().all()
                .when().get("/stations")  //TODO /stations 가 다른 path로 변경된다면?
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철역을_삭제한다(long stationId) {
        return RestAssured.given().log().all()
                .pathParam("id", stationId)
                .when().delete("/stations/{id}")
                .then().log().all()
                .extract();
    }
}
