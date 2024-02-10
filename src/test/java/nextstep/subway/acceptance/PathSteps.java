package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class PathSteps {
    private PathSteps() {
    }

    public static ExtractableResponse<Response> 경로_조회를_요청한다(final Long source, final Long target) {
        Map<String, String> params = createPathRequestPixture(source, target);
        return RestAssured.given().log().all().body(params).contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("paths", params)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }

    public static Map<String, String> createPathRequestPixture(final Long source, final Long target) {
        Map<String, String> params = new HashMap<>();
        params.put("source", String.valueOf(source));
        params.put("target", String.valueOf(target));
        return params;
    }
}
