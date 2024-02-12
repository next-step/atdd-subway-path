package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.utils.DataBaseCleaner;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.given;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public abstract class BaseTest {

    @Autowired
    private DataBaseCleaner dataBaseCleaner;

    @BeforeEach
    void setUp() {
        dataBaseCleaner.execute();
    }

    protected ExtractableResponse<Response> callPostApi(final Object requestBody, final String path) {
        final ExtractableResponse<Response> response =
                given()
                    .log().all()
                    .body(requestBody)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                    .post(path)
                .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .log().all()
                .extract();

        return response;
    }

    protected ExtractableResponse<Response> callPostApi(final Object requestBody, final String path, final Long pathVariable) {
        final ExtractableResponse<Response> response =
                given()
                    .log().all()
                    .body(requestBody)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                    .post(path, pathVariable)
                .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .log().all()
                .extract();

        return response;
    }

    protected void callPostApiWithServerError(final Object requestBody, final String path, final Long pathVariable) {
        given()
            .log().all()
            .body(requestBody)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when()
            .post(path, pathVariable)
        .then()
            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .log().all();
    }

    protected ExtractableResponse<Response> callGetApi(final String path) {
        final ExtractableResponse<Response> response =
                given()
                    .log().all()
                .when()
                    .get(path)
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .log().all()
                .extract();

        return response;
    }

    protected ExtractableResponse<Response> callGetApi(final String path, final Long pathVariable) {
        final ExtractableResponse<Response> response =
                given()
                    .log().all()
                .when()
                    .get(path, pathVariable)
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .log().all()
                .extract();

        return response;
    }

    protected ExtractableResponse<Response> callPutApi(final Object requestBody, final String path, final Long pathVariable) {
        final ExtractableResponse<Response> response =
                given()
                    .log().all()
                    .body(requestBody)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                    .put(path, pathVariable)
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .log().all()
                .extract();

        return response;
    }

    protected void callDeleteApi(final String path, final Long pathVariable) {
        given()
        .when()
            .delete(path, pathVariable)
        .then()
            .statusCode(HttpStatus.NO_CONTENT.value())
            .log().all();
    }

    protected void callDeleteApi(final String paramName, final Object Param, final String path, final Long pathVariable) {
        given()
            .log().all()
            .param(paramName, Param)
        .when()
            .delete(path, pathVariable)
        .then()
            .statusCode(HttpStatus.NO_CONTENT.value())
            .log().all();
    }

    protected void callDeleteApiWithServerError(final String paramName, final Object Param, final String path, final Long pathVariable) {
        given()
            .log().all()
            .param(paramName, Param)
        .when()
            .delete(path, pathVariable)
        .then()
            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .log().all();
    }

    protected Long getIdFromApiResponse(final ExtractableResponse<Response> response) {
        final String location = response.header("Location");
        final String entityId = location.replaceAll(".*/(\\d+)$", "$1");

        return Long.parseLong(entityId);
    }

}

