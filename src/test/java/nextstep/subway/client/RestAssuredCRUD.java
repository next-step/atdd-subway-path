package nextstep.subway.client;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class RestAssuredCRUD implements ApiCRUD {

    @Override
    public <T> ExtractableResponse<Response> create(String path, T jsonBody) {
        return requestWithBody(Method.POST, jsonBody, path);
    }

    @Override
    public ExtractableResponse<Response> read(String path) {
        return request(Method.GET, path);
    }

    @Override
    public <T> ExtractableResponse<Response> read(String path, T pathVariable) {
        return request(Method.GET, path, pathVariable);
    }

    @Override
    public <T> ExtractableResponse<Response> update(String path, T jsonBody) {
        return requestWithBody(Method.PUT, jsonBody, path);
    }

    @Override
    public <T> ExtractableResponse<Response> delete(String path, T pathVariable) {
        return request(Method.DELETE, path, pathVariable);
    }

    private ExtractableResponse<Response> request(Method method, String path) {
        return RestAssured.given().log().all()
                .when().request(method, path)
                .then().log().all()
                .extract();
    }

    private <T> ExtractableResponse<Response> request(Method method, String path, T pathVariable) {
        return RestAssured.given().log().all()
                .when().request(method, path, pathVariable)
                .then().log().all()
                .extract();
    }

    private <T> ExtractableResponse<Response> requestWithBody(Method method, T jsonBody, String path) {
        return RestAssured.given().log().all()
                .body(jsonBody)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().request(method, path)
                .then().log().all()
                .extract();
    }

}
