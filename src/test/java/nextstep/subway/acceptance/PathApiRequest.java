package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.Map;

public class PathApiRequest {

    public static final String URL_PREFIX = "/path";

    public static Response 최단거리를_반환한다(Long 출발역, Long 도착역) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .params(Map.of("source", 출발역, "target", 도착역))
                .when().get(URL_PREFIX)
                .then().log().all()
                .extract().response();
    }


}
