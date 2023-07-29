package nextstep.subway.utils;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;


public class ApiRequest {
    private ApiRequest() {
    }

    public static ExtractableResponse<Response> get(String path) {
        return RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(path)
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> post(String path, Object params) {
        return RestAssured.given()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(path)
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> put(String path, Object params) {
        return RestAssured.given()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(path)
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> delete(String path) {
        return RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete(path)
                .then()
                .log().all()
                .extract();
    }
}
