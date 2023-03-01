package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

public class PathSteps {
    public static ExtractableResponse<Response> 거리_경로_조회_요청(Long source, Long target) {
        return RestAssured.given().log().all().
                accept(MediaType.APPLICATION_JSON_VALUE).
                when().get("/paths?source={sourceId}&target={targetId}", source, target).
                then().log().all().
                extract();
    }
}
