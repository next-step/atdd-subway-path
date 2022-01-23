package nextstep.subway.utils;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.Map;

public class HttpRequestTestUtil {

    public static ExtractableResponse<Response> postRequest(String url, Map<String,String> param){
         return RestAssured
                .given()
                .body(param)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(url)
                .then().extract();
    }

    public static ExtractableResponse<Response> putRequest(String url, Map<String,String> param){
        return RestAssured
                .given()
                .body(param)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(url)
                .then().extract();
    }

    public static ExtractableResponse<Response> deleteRequest(String url){
        return RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete(url)
                .then().extract();
    }

    public static ExtractableResponse<Response> getRequest(String url){
        return RestAssured
                .given()
                .when().get(url)
                .then().extract();
    }

}
