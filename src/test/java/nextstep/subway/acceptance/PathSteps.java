package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.applicaion.dto.request.PathRequest;
import org.springframework.http.MediaType;

public class PathSteps {

    public static ExtractableResponse<Response> 지하철_최단경로_조회(PathRequest request) {
        return RestAssured
                .given().log().all()
                .queryParam("source", request.getSource())
                .queryParam("target", request.getTarget())
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/paths")
                .then().log().all().extract();
    }

}
