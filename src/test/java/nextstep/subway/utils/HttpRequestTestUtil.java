package nextstep.subway.utils;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.Map;

public class HttpRequestTestUtil {

    public static ExtractableResponse<Response> 포스트_요청(String url, Map<String, Object> param) {
        return RestAssured
                .given().log().all()
                .body(param)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(url)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 풋_요청(String url, Map<String, Object> param) {
        return RestAssured
                .given().log().all()
                .body(param)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(url)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 딜리트_요청(String url) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete(url)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 겟_요청(String url) {
        return RestAssured
                .given().log().all()
                .when().get(url)
                .then().log().all().extract();
    }

}
