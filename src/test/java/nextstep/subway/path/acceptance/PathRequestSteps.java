package nextstep.subway.path.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

public class PathRequestSteps {

    public static ExtractableResponse<Response> 지하철_최단_경로_조회_요청(long source, long target) {
        return RestAssured
                .given().log().all()
                .param("source", source)
                .param("target", target)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .get("/paths")
                .then().log().all()
                .extract();
    }
}
