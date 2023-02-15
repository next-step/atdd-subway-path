package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.applicaion.dto.LineResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class LineSteps {
    public static ExtractableResponse<Response> 지하철_노선_생성_요청(String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        return RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured
                .given().log().all()
                .when().get("/lines")
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_조회_요청(ExtractableResponse<Response> createResponse) {
        return RestAssured
                .given().log().all()
                .when().get(createResponse.header("location"))
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_조회_요청(Long id) {
        return RestAssured
                .given().log().all()
                .when().get("/lines/{id}", id)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_생성_요청(Map<String, Object> params) {
        return RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 지하철_노선에_지하철_구간_생성_요청(Long lineId, Map<String, Object> params) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().post("/lines/{lineId}/sections", lineId)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 지하철_노선에_지하철_구간_제거_요청(Long lineId, Long stationId) {
        return RestAssured.given().log().all()
                .when().delete("/lines/{lineId}/sections?stationId={stationId}", lineId, stationId)
                .then().log().all().extract();
    }

    public static void 지하철_노선_정보_수정_요청(final ExtractableResponse<Response> createResponse, final Map<String, String> params) {
        RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(createResponse.header("location"))
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_삭제_요청(ExtractableResponse<Response> createResponse) {
        return RestAssured
                .given().log().all()
                .when().delete(createResponse.header("location"))
                .then().log().all().extract();
    }

    public static void 지하철_노선_목록_중_생성한_노선_조회됨(final ExtractableResponse<Response> lineResponse, final ExtractableResponse<Response> createResponse) {
        final List<Long> 지하철_노선_목록_ID = 지하철_노선_목록_중_ID_목록_추출함(lineResponse);
        final Long 지하철_노선_생성_ID = 응답_헤더_ID_추출함(createResponse);

        assertAll(
                () -> assertThat(lineResponse.response().statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(지하철_노선_목록_ID).contains(지하철_노선_생성_ID)
        );
    }

    private static List<Long> 지하철_노선_목록_중_ID_목록_추출함(ExtractableResponse<Response> lineResponse) {
        return lineResponse.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
    }

    private static long 응답_헤더_ID_추출함(final ExtractableResponse<Response> createResponse) {
        return Long.parseLong(createResponse.header("Location").split("/")[2]);
    }

    public static void 지하철_노선_목록_조회됨(final ExtractableResponse<Response> lineResponse, final int countOfLine) {
        final JsonPath 지하철_목록_응답_경로 = lineResponse.response().body().jsonPath();

        assertAll(
                () -> assertThat(lineResponse.response().statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(지하철_목록_응답_경로.getList("")).hasSize(countOfLine)
        );
    }

    public static void 지하철_노선_조회됨(final ExtractableResponse<Response> response, final String lineName, final String lineColor, final int countOfStation) {
        final JsonPath jsonPath = response.response().body().jsonPath();

        assertAll(
                () -> assertThat(response.response().statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(jsonPath.getString("name")).isEqualTo(lineName),
                () -> assertThat(jsonPath.getString("color")).isEqualTo(lineColor),
                () -> assertThat(jsonPath.getList("stations")).hasSize(countOfStation)
        );
    }

    public static Map<String, Object> createSectionCreateParams(final Long upStationId, final Long downStationId, final Integer distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);
        return params;
    }

    public static Map<String, Object> createLineCreateParams(final String name, final String color, final Long upStationId, final Long downStationId, final Integer distance) {
        Map<String, Object> lineCreateParams;
        lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", name);
        lineCreateParams.put("color", color);
        lineCreateParams.put("upStationId", upStationId);
        lineCreateParams.put("downStationId", downStationId);
        lineCreateParams.put("distance", distance);
        return lineCreateParams;
    }
}
