package nextstep.subway.commons;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.Map;

public class RestAssuredUtils {

    private RestAssuredUtils() {}

    public static ExtractableResponse<Response> get_요청(String url) {
        return RestAssured
                .given().log().all()
                .when().get(url)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> post_요청(String url, Map<String, String> params) {
        return RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(url)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> put_요청(String url, Map<String, String> params) {
        return RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(url)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> delete_요청(String url) {
        return RestAssured
                .given().log().all()
                .when().delete(url)
                .then().log().all().extract();
    }


}
