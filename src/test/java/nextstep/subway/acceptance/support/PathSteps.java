package nextstep.subway.acceptance.support;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PathSteps {
    public static ExtractableResponse<Response> 최단경로_조회_요청(Long sourceId, Long targetId) {
        return RestAssured
            .given().log().all()
            .when().get("/paths?source={source}&target={target}", sourceId, targetId)
            .then().log().all().extract();
    }
}
