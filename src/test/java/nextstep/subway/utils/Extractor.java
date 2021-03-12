package nextstep.subway.utils;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.springframework.http.MediaType;

public class Extractor {

    public static ExtractableResponse<Response> get(String path) {

        return when(restAssured()).get(path)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> post(String path, Object body) {
        return when(restAssured()
                .body(body)
                .contentType(MediaType.APPLICATION_JSON_VALUE)).post(path)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> put(String path, Object body) {
        return when(restAssured()
                .body(body)
                .contentType(MediaType.APPLICATION_JSON_VALUE)).put(path)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> delete(String path) {
        return when(restAssured()).delete(path)
                .then().log().all().extract();
    }

    private static RequestSpecification when(RequestSpecification requestSpecification) {
        return requestSpecification
                .when();
    }

    private static RequestSpecification restAssured() {
        return RestAssured
                .given().log().all();
    }

}
