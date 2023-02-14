package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;

public class PathSteps {
    
    public static ExtractableResponse<Response> 최단_경로_조회(final Long source, final Long target) {
        return RestAssured
                .given().log().all()
                .when()
                .get("paths?source={source}&target={target}", source, target)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }
}
