package nextstep.subway.utils;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class RestAssuredCRUD {

    public static ExtractableResponse<Response> postRequest(String path, Object request) {
        return RestAssured.given().log().all()
            .body(request)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post(path)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> postRequestWithOAuth(String path, Object request, String token) {
        return RestAssured.given().log().all()
            .body(request)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .auth().oauth2(token)
            .when()
            .post(path)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> get(String path) {
        return RestAssured.given().log().all()
            .when()
            .get(path)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> getWithOAuth(String path, String token) {
        return RestAssured.given().log().all()
            .auth().oauth2(token)
            .when()
            .get(path)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> putRequest(String path, Object request) {
        return RestAssured.given().log().all()
            .body(request)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put(path)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> putRequestWithAOuth(String path, Object request, String token) {
        return RestAssured.given().log().all()
            .body(request)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .auth().oauth2(token)
            .when()
            .put(path)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> delete(String path) {
        return RestAssured.given().log().all()
            .when()
            .delete(path)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> deleteWithOAuth(String path, String token) {
        return RestAssured.given().log().all()
            .auth().oauth2(token)
            .when()
            .delete(path)
            .then().log().all()
            .extract();
    }

    public static void 응답결과가_OK(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 응답결과가_NO_CONTENT(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    public static void 응답결과가_BAD_REQUEST(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static void 응답결과가_CREATED(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    public static void 응답결과를_확인한다(ExtractableResponse<Response> response, HttpStatus status) {
        assertThat(response.statusCode()).isEqualTo(status.value());
    }
}
