package nextstep.subway.path;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class PathApiRequester {

    public static ExtractableResponse<Response> getPaths(long source, long target) {
        return RestAssured.given().log().all()
            .queryParam("source", source)
            .queryParam("target", target)
            .when().get("/paths")
            .then().log().all()
            .extract();
    }
}
