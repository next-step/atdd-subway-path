package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.acceptance.StationFixture.STATION_NAME;

public class StationSteps {

    public static Long 지하철_역_생성_되어있음(final String name) {
        return 지하철_역_생성을_요청한다(name).jsonPath().getLong("id");
    }

    public static ExtractableResponse<Response> 지하철_역_생성을_요청한다(final String name) {
        final Map<String, String> params = 지하철_역_생성_데이터를_만든다(name);
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
        return response;
    }

    private static Map<String, String> 지하철_역_생성_데이터를_만든다(final String name) {
        final Map<String, String> params = new HashMap<>();
        params.put(STATION_NAME, name);
        return params;
    }

    public static ExtractableResponse<Response> 지하철_역_목록_조회를_요청한다() {
        return RestAssured.given().log().all()
                .when()
                .get("/stations")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_역_삭제를_요청한다(final Long stationId) {
        return RestAssured.given().log().all()
                .when()
                .delete("/stations/" + stationId)
                .then().log().all()
                .extract();
    }
}
