package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

public class PathSteps {

    public static ExtractableResponse<Response> 지하철역_경로_조회_요청() {
        return RestAssured.given().log().all()
                .queryParam("source", 16)
                .queryParam("target", 3)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/paths")
                .then().log().all()
                .extract();
    }
}
