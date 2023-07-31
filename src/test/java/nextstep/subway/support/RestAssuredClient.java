package nextstep.subway.support;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.Map;

/**
 * <pre>
 * RestAssured를 통한 HTTP Request를 도와주는 Wrapper 클래스입니다.
 * POST / GET / PUT / PATCH / DELETE HTTP method에 대해 지원합니다.
 * </pre>
 */
public class RestAssuredClient {
    /**
     * <pre>
     * path에
     * POST 방식으로
     * RestAssured를 통해
     * requestBody를 담아
     * HTTP 요청을 보낼 때 사용합니다.
     * </pre>
     *
     * @param path
     * @param requestBody
     * @return ExtractableResponse
     */
    public static <T> ExtractableResponse<Response> post(String path, T requestBody) {
        return RestAssured
                .given()
                    .log().all()
                    .body(requestBody)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                    .post(path)
                .then()
                    .log().all()
                    .extract();
    }

    /**
     * <pre>
     * path에
     * GET 방식으로
     * RestAssured를 통해
     * HTTP 요청을 보낼 때 사용합니다.
     * </pre>
     *
     * @param path
     * @return ExtractableResponse
     */
    public static ExtractableResponse<Response> get(String path) {
        return RestAssured
                .given()
                    .log().all()
                .when()
                    .get(path)
                .then()
                    .log().all()
                    .extract();
    }

    /**
     * <pre>
     * path에
     * GET 방식으로
     * RestAssured를 통해
     * query parameter와 함께
     * HTTP 요청을 보낼 때 사용합니다.
     * </pre>
     *
     * @param basePath
     * @param queryParamMap
     * @return ExtractableResponse
     */
    public static ExtractableResponse<Response> get(String basePath, Map<String, ?> queryParamMap) {
        return RestAssured
                .given()
                    .log().all()
                    .queryParams(queryParamMap)
                .when()
                    .get(basePath)
                .then()
                    .log().all()
                    .extract();
    }

    /**
     * <pre>
     * path에
     * PUT 방식으로
     * RestAssured를 통해
     * requestBody를 담아
     * HTTP 요청을 보낼 때 사용합니다.
     * </pre>
     *
     * @param path
     * @param requestBody
     * @return ExtractableResponse
     */
    public static <T> ExtractableResponse<Response> put(String path, T requestBody) {
        return RestAssured
                .given()
                    .log().all()
                    .body(requestBody)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                    .put(path)
                .then()
                    .log().all()
                    .extract();
    }

    /**
     * <pre>
     * path에
     * DELETE 방식으로
     * RestAssured를 통해
     * HTTP 요청을 보낼 때 사용합니다.
     * </pre>
     *
     * @param path
     * @return ExtractableResponse
     */
    public static ExtractableResponse<Response> delete(String path) {
        return RestAssured
                .given()
                    .log().all()
                .when()
                    .delete(path)
                .then()
                    .log().all()
                    .extract();
    }

}
