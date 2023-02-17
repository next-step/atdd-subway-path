package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.fixture.FieldFixture.노선_내_역_아이디;
import static nextstep.subway.utils.JsonPathUtil.리스트로_추출;
import static org.assertj.core.api.Assertions.assertThat;

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

    public static ExtractableResponse<Response> 지하철_노선에_지하철_구간_제거_요청(Long lineId, Long stationId) {
        return RestAssured.given().log().all()
                .when().delete("/lines/{lineId}/sections?stationId={stationId}", lineId, stationId)
                .then().log().all().extract();
    }


    public static void 노선_조회가_성공한다(ExtractableResponse<Response> 노선_조회_결과) {
        assertThat(노선_조회_결과.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 노선에_역이_포함되어있다(ExtractableResponse<Response> 노선_조회_결과, Long... 역_id_목록) {
        assertThat(리스트로_추출(노선_조회_결과, 노선_내_역_아이디, Long.class))
                .containsAnyOf(역_id_목록);
    }

    public static void 노선에_역이_순서대로_포함되어있다(ExtractableResponse<Response> 노선_조회_결과, Long... 역_id_목록) {
        assertThat(리스트로_추출(노선_조회_결과, 노선_내_역_아이디, Long.class))
                .containsExactly(역_id_목록);
    }

    public static void 노선에_역이_포함되어_있지않다(ExtractableResponse<Response> 노선_조회_결과, Long... 역_id_목록) {
        assertThat(리스트로_추출(노선_조회_결과, 노선_내_역_아이디, Long.class))
                .doesNotContain(역_id_목록);
    }

    public static void 노선에_상행_종점역과_일치한다(ExtractableResponse<Response> 노선_조회_결과, Long 역_id) {
        assertThat(리스트로_추출(노선_조회_결과, 노선_내_역_아이디, Long.class))
                .first().isEqualTo(역_id);
    }
}
