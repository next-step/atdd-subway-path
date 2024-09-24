package nextstep.subway.steps;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

public class PathSteps {

    public static ExtractableResponse<Response> 지하철_경로_조회_요청(Long 출발역, Long 도착역) {
        return RestAssured.given().log().all()
                .queryParam("source", 출발역)
                .queryParam("target", 도착역)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/paths")
                .then().log().all()
                .extract();
    }
}
