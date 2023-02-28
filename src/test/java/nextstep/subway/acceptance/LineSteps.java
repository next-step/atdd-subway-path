package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

public class LineSteps extends Steps {

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

    public static Long 지하철_노선_생성_요청(final String name, final String color, final long upStationId,
                                                             final long downStationId, final int distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all().extract();
        return response.jsonPath().getLong("id");
    }

    public static void 지하철_노선이_정상적으로_추가되었는지_확인(final ExtractableResponse<Response> response, final String name) {
        응답_코드_검증(response, HttpStatus.CREATED);

        ExtractableResponse<Response> listResponse = 지하철_노선_목록_조회_요청();
        assertThat(listResponse.jsonPath().getList("name")).contains(name);
    }

    public static ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured
                .given().log().all()
                .when().get("/lines")
                .then().log().all().extract();
    }

    public static void 지하철_노선_목록에_노선이_존재하는지_확인(final ExtractableResponse<Response> response, final String... names) {
        응답_코드_검증(response, HttpStatus.OK);
        assertThat(response.jsonPath().getList("name")).contains(names);
    }

    public static ExtractableResponse<Response> 지하철_노선_조회_요청(ExtractableResponse<Response> createResponse) {
        return RestAssured
                .given().log().all()
                .when().get(createResponse.header("location"))
                .then().log().all().extract();
    }

    public static void 조회한_지하철_노선의_정보가_맞는지_확인(final ExtractableResponse<Response> response, final String name) {
        응답_코드_검증(response, HttpStatus.OK);
        assertThat(response.jsonPath().getString("name")).isEqualTo(name);
    }

    public static ExtractableResponse<Response> 지하철_노선_조회_요청(Long id) {
        return RestAssured
                .given().log().all()
                .when().get("/lines/{id}", id)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_조회_요청함(final Long id) {
        final ExtractableResponse<Response> response = 지하철_노선_조회_요청(id);
        응답_코드_검증(response, HttpStatus.OK);
        return response;
    }

    public static void 지하철_노선_수정_요청(final ExtractableResponse<Response> createResponse, final String name, final String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(createResponse.header("location"))
                .then().log().all()
                .assertThat().statusCode(HttpStatus.OK.value())
                .extract();
    }

    public static void 지하철_노선이_정상적으로_수정되었는지_확인(final ExtractableResponse<Response> createResponse, final String color) {
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(createResponse);
        응답_코드_검증(response, HttpStatus.OK);
        assertThat(response.jsonPath().getString("color")).isEqualTo(color);
    }

    public static ExtractableResponse<Response> 지하철_노선_삭제_요청(final ExtractableResponse<Response> createResponse) {
        return RestAssured
                .given().log().all()
                .when().delete(createResponse.header("location"))
                .then().log().all().extract();
    }

    public static void 지하철이_정상적으로_삭제되었는지_확인(final ExtractableResponse<Response> response) {
        응답_코드_검증(response, HttpStatus.NO_CONTENT);
    }

    public static ExtractableResponse<Response> 지하철_노선_생성_요청(Map<String, String> params) {
        return RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 지하철_노선에_지하철_구간_생성_요청(Long lineId, Map<String, String> params) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().post("/lines/{lineId}/sections", lineId)
                .then().log().all().extract();
    }

    public static void 지하철_노선에_역들이_순차적으로_존재하는지_확인(final Long lineId, final Long... stationIds) {
        final ExtractableResponse<Response> response = 지하철_노선_조회_요청함(lineId);
        final List<Long> 지하철_노선의_역들 = response.jsonPath().getList("stations.id", Long.class);
        assertThat(지하철_노선의_역들).containsExactly(stationIds);
    }

    public static ExtractableResponse<Response> 지하철_노선에_지하철_구간_제거_요청(Long lineId, Long stationId) {
        return RestAssured.given().log().all()
                .when().delete("/lines/{lineId}/sections?stationId={stationId}", lineId, stationId)
                .then().log().all().extract();
    }
}
