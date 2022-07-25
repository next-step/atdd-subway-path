package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class PathSteps {

    public static ExtractableResponse<Response> 최단_경로_조회(long 출발역, long 도착역) {
        return RestAssured.given().log().all()
                .queryParam("source", 출발역)
                .queryParam("target", 도착역)
                .when()
                .get("/paths")
                .then().log().all()
                .extract();
    }
}
