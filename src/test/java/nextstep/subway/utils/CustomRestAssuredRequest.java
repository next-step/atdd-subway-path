package nextstep.subway.utils;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.Map;

public class CustomRestAssuredRequest {

    public static ExtractableResponse<Response> requestGet(
            ExtractableResponse<Response> createResponse) {
        return RestAssured.given().log().all()
                .when().get(createResponse.header("location"))
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> requestGet(String path) {
        return RestAssured.given().log().all()
                .when().get(path)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> requestGet(String path, Map<String, ?> pathParams) {
        return RestAssured.given().log().all()
                .when().get(path, pathParams)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> requestPost(String path,
                                                            Map<String, ?> bodyParams) {
        return RestAssured.given().log().all().body(bodyParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(path)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> requestPost(String path, Map<String, ?> pathParams,
                                                            Map<String, ?> bodyParams) {
        return RestAssured.given().log().all().body(bodyParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(path, pathParams)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> requestPut(String path, Map<String, ?> bodyParams) {
        return RestAssured.given().log().all().body(bodyParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(path)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> requestPut(String path, Map<String, ?> pathParams,
                                                           Map<String, ?> bodyParams) {
        return RestAssured.given().log().all().body(bodyParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(path, pathParams)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> requestDelete(String path) {
        return RestAssured.given().log().all()
                .when().delete(path)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> requestDelete(String path,
                                                              Map<String, ?> pathParams) {
        return RestAssured.given().log().all()
                .when().delete(path, pathParams)
                .then().log().all().extract();
    }

}
