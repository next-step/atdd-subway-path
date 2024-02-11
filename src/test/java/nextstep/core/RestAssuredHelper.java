package nextstep.core;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.Map;

public class RestAssuredHelper {
    private RestAssuredHelper() {
    }

    public static ExtractableResponse<Response> get(final String path) {
        return RestAssured
                .given()
                .when().get(path)
                .then().extract();
    }

    public static ExtractableResponse<Response> get(final String path, final Map<String, ?> parametersMap) {
        return RestAssured
                .given()
                .given().queryParams(parametersMap)
                .when().get(path)
                .then().extract();
    }

    public static ExtractableResponse<Response> getById(final String path, final Long id) {
        return RestAssured
                .given().pathParam("id", id)
                .when().get(path + "/{id}")
                .then().extract();
    }

    public static ExtractableResponse<Response> post(final String path, final Object body) {
        return RestAssured
                .given()
                .body(body)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(path)
                .then().extract();
    }

    public static ExtractableResponse<Response> post(final String path, final Long pathVariable, final Object body) {
        return RestAssured
                .given().pathParam("id", pathVariable)
                .body(body)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(path)
                .then().extract();
    }

    public static ExtractableResponse<Response> put(final String path, final Long id, final Object body) {
        return RestAssured
                .given().pathParam("id", id)
                .body(body)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(path + "/{id}")
                .then().extract();
    }

    public static ExtractableResponse<Response> deleteById(final String path, final Long id) {
        return RestAssured
                .given().pathParam("id", id)
                .when().delete(path + "/{id}")
                .then().extract();
    }

    public static ExtractableResponse<Response> deleteByIdWithParam(final String path, final Long id, final Map<String, ?> parametersMap) {
        return RestAssured
                .given().pathParam("id", id)
                .queryParams(parametersMap)
                .when().delete(path)
                .then().extract();
    }

    public static Long getIdFrom(final ExtractableResponse<Response> response) {
        return response.jsonPath().getLong("id");
    }

    public static <T> T findObjectFrom(final ExtractableResponse<Response> response, final Long id, final Class<T> type) {
        return response.jsonPath().getObject(String.format("find {it.id==%d}", id), type);
    }
}
