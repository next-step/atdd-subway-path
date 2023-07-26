package nextstep.subway.fixture.acceptance.when;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.MediaType;

public abstract class PathApiFixture {

    public static ExtractableResponse<Response> 경로조회_요청(long source, long target) {

        Map<String, Object> params = new HashMap<>();
        params.put("source", source);
        params.put("target", target);

        return RestAssured
            .given().log().all()
            .queryParams(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().get("/paths")
            .then().log().all()
            .extract();
    }
}
