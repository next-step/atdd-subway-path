package nextstep.subway.step;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

public class PathStep {

    private static final String PATH = "/paths";

    public static ExtractableResponse<Response> 경로_조회(Long source, Long target) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .queryParam("source", source)
                .queryParam("target", target)
                .when()
                .get(PATH)
                .then().log().all()
                .extract();
    }
}
