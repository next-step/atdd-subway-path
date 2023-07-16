package nextstep.subway.utils;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;

public class AcceptanceTestUtils {

    public static <T> ExtractableResponse<Response> post(String path, T request) {
        return RestAssured
                .given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(path)
                .then().log().all()
                .extract();
    }

    public static <T> ExtractableResponse<Response> post(String path, Long id, T request) {
        return RestAssured
                .given().log().all()
                .pathParam("id", id)
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(path)
                .then().log().all()
                .extract();
    }

    public static <T> List<T> get(String path, String key, Class<T> genericType) {
        return RestAssured.given().log().all()
                .when().get(path)
                .then().log().all()
                .extract().jsonPath().getList(key, genericType);
    }

    public static ExtractableResponse<Response> get(String path, Long id) {
        return RestAssured
                .given().log().all()
                .pathParam("id", id)
                .when().get(path)
                .then().log().all()
                .extract();
    }

    public static <T> ExtractableResponse<Response> put(String path, Long lineId, T request) {
        return RestAssured
                .given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("id", lineId)
                .when().put(path)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> delete(String path, Long id) {
        return RestAssured
                .given().log().all()
                .pathParam("id", id)
                .when().delete(path)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> get(String path, Map<String, Long> queryParams) {
        return RestAssured
                .given().log().all()
                .queryParams(queryParams)
                .when().get(path)
                .then().log().all()
                .extract();
    }

}
