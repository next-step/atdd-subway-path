package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static nextstep.subway.acceptance.AssertionSteps.응답_코드_검증;
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

    public static void 구간_생성_요청_실패(ExtractableResponse<Response> response) {
        응답_코드_검증(response, HttpStatus.BAD_REQUEST);
    }

    public static void 구간_삭제_요청_실패(ExtractableResponse<Response> response) {
        응답_코드_검증(response, HttpStatus.CONFLICT);
    }

    public static void 구간_생성_요청_후_역_목록_확인(ExtractableResponse<Response> response, Long... expected) {
        응답_코드_검증_역_목록_확인(response, expected);
    }

    public static void 구간_삭제_요청_후_역_목록_확인(ExtractableResponse<Response> response, Long... expected) {
        응답_코드_검증_역_목록_확인(response, expected);
    }

    private static void 응답_코드_검증_역_목록_확인(ExtractableResponse<Response> response, Long[] expected) {
        List<Long> idsOfStations = response.jsonPath().getList("stations.id", Long.class);

        assertAll(
                () -> 응답_코드_검증(response, HttpStatus.OK),
                () -> 지하철_역_목록_순서_일치_검증(idsOfStations, expected)
        );
    }

    private static void 지하철_역_목록_순서_일치_검증(List<Long> idsOfStations, Long... expected) {
        assertThat(idsOfStations).containsExactly(expected);
    }
}
