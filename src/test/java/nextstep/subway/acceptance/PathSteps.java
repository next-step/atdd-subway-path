package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class PathSteps {

    public static ExtractableResponse<Response> getPath(Long source, Long target) {
        return RestAssured.given().log().all()
                .queryParam("source", source)
                .queryParam("target", target)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/paths")
                .then().log().all()
                .extract();
    }
}
