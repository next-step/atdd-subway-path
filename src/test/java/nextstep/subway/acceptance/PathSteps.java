package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

public class PathSteps {

    public static ExtractableResponse<Response> 최단_경로_조회하기(Long source, Long target) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .queryParam("source", source)
                .queryParam("target", target)
                .when().get("/paths")
                .then().log().all().extract();
    }
}
