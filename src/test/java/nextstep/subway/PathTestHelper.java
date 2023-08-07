package nextstep.subway;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class PathTestHelper {
    public static ExtractableResponse<Response> findPath(Long source, Long target) {
        return RestAssured
                .given()
                    .log().all()
                .when()
                    .get("/paths?source={source}&target={target}", source, target)
                .then()
                    .log().all()
                    .extract();
    }
}
