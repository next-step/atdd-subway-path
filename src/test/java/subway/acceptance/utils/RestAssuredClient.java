package subway.acceptance.utils;

import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.springframework.http.MediaType;

public class RestAssuredClient {

    public static <T> ValidatableResponse requestPost(String path, T params) {
        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post(path)
            .then().log().all();
    }

    public static ValidatableResponse requestGet(String path) {
        return RestAssured.given().log().all()
            .when().get(path)
            .then().log().all();
    }

    public static <T> ValidatableResponse requestPut(String path, T params) {
        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().put(path)
            .then().log().all();
    }

    public static ValidatableResponse requestDelete(String path) {
        return RestAssured.given().log().all()
            .when().delete(path)
            .then().log().all();
    }
}
