package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

public class PathSteps {

    public static ExtractableResponse<Response> 지하철_경로_조회_요청(long sourceStation, long targetStation) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .queryParam("source", sourceStation)
                .queryParam("target", targetStation)
                .get("/paths")
                .then().log().all().extract();
    }
}
