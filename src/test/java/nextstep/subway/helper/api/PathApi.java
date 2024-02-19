package nextstep.subway.helper.api;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class PathApi {
    final static private String routePrefix = "/paths";

    public static ExtractableResponse<Response> 최단경로조회요청(Long 출발역ID, Long 도착역ID) {
        return RestAssured.given().log().all()
                .queryParam("source", 출발역ID)
                .queryParam("target", 도착역ID)
                .when().get(routePrefix)
                .then().log().all()
                .extract();
    }
}
