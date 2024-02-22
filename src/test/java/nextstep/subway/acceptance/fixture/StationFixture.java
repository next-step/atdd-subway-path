package nextstep.subway.acceptance.fixture;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.station.StationRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;


public class StationFixture {
    public static ExtractableResponse<Response> 지하철역_생성_요청(String name) {
        return RestAssured
            .given()
            .body(new StationRequest(name))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/stations")
            .then()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철역_목록_조회() {
        return RestAssured
            .given()
            .when()
            .get("/stations")
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract();
    }
}
