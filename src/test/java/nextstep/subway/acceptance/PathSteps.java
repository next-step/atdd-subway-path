package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;

public class PathSteps {

    public static ExtractableResponse<Response> 지하철_경로_조회_요청(Long 출발역, Long 도착역) {
        return given()
                .params("source", 출발역, "target", 도착역)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/paths")
                .then().log().all()
                .extract();
    }
}
