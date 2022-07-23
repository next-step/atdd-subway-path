package nextstep.subway.acceptance.steps;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.Map;

public class PathSteps {

    public static ExtractableResponse<Response> 지하철_경로_최단_거리_조회_요청(Long source, Long target) {
        return RestAssured.given().log().all()
                .params(Map.of(
                        "source", source,
                        "target", target
                ))
                .when().get("/paths")
                .then().log().all()
                .extract();
    }
}
