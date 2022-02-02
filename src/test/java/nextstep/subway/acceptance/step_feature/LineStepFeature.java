package nextstep.subway.acceptance.step_feature;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.applicaion.dto.LineAndSectionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

public class LineStepFeature {

    public static final String 신분당선_이름 = "신분당선";
    public static final String 신분당선_색 = "red";
    public static final String 이호선_이름 = "2호선";

    public static LineAndSectionResponse 지하철_노선_생성_조회_요청(Map<String, String> lineParams) {
        ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청(lineParams);
        String uri = createResponse.header("Location");

        ExtractableResponse<Response> response = callGetLinesByUri(uri);
        return response.as(LineAndSectionResponse.class);
    }

    public static ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured.given()
                .log()
                .all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines")
                .then()
                .log()
                .all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_조회_요청(long id) {
        return RestAssured.given()
                .log()
                .all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines/{id}", id)
                .then()
                .log()
                .all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_생성_요청(Map<String, String> params) {
        return RestAssured.given()
                .log()
                .all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then()
                .log()
                .all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_수정_요청(Map<String, String> params) {
        return RestAssured.given()
                .log()
                .all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/{id}", params.get("id"))
                .then()
                .log()
                .all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_삭제_요청(long id) {
        return RestAssured.given()
                .log()
                .all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/lines/{id}", id)
                .then()
                .log()
                .all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선에_지하철_구간_생성_요청(long lineId, long upStationId, long downStationId, int distance) {
        Map<Object, Object> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        return RestAssured.given()
                .log()
                .all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/{id}/sections", lineId)
                .then()
                .log()
                .all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선에_지하철_구간_제거_요청(long lineId, long stationId) {
        return RestAssured.given()
                .log()
                .all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/lines/{lineId}/sections?stationId={stationId}", lineId, stationId)
                .then()
                .log()
                .all()
                .extract();
    }

    public static Map<String, String> 노선_생성_Param_생성(String name, String color, Long upStationId,
                                                     Long downStationId, int distance) {
        Map<String, String> params = new HashMap();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", String.valueOf(upStationId));
        params.put("downStationId", String.valueOf(downStationId));
        params.put("distance", String.valueOf(distance));

        return params;
    }

    public static Map<String, String> 노성_수정_Param_생성(long id, String name, String color) {
        Map<String, String> params = new HashMap();
        params.put("id", String.valueOf(id));
        params.put("name", name);
        params.put("color", "blue");

        return params;
    }

    public static void 노선_생성_응답상태_검증(ExtractableResponse<Response> response) {
        노선_응답_상태코드_검증(response.statusCode(), HttpStatus.CREATED);
        assertThat(response.header("Location")).isNotBlank();
    }

    public static void 노선_생성_실패_응답상태_검증(ExtractableResponse<Response> response) {
        노선_응답_상태코드_검증(response.statusCode(), HttpStatus.BAD_REQUEST);
    }

    public static void 노선_조회_응답상태_검증(ExtractableResponse<Response> response) {
        노선_응답_상태코드_검증(response.statusCode(), HttpStatus.OK);
    }

    public static void 노선_삭제_응답상태_검증(ExtractableResponse<Response> response) {
        노선_응답_상태코드_검증(response.statusCode(), HttpStatus.NO_CONTENT);
    }

    public static void 응답받은_노선의_상세_값_확인(ExtractableResponse<Response> response) {
        List<LineAndSectionResponse> responses = response.jsonPath()
                .getList(".", LineAndSectionResponse.class);
        LineAndSectionResponse response1 = responses.get(0);
        LineAndSectionResponse response2 = responses.get(1);
        assertThat(response1.getLineName()).isEqualTo(신분당선_이름);
        assertThat(response2.getLineName()).isEqualTo(이호선_이름);
        assertThat(response1.getStations().size()).isEqualTo(3);
        assertThat(response2.getStations().size()).isEqualTo(2);
    }

    public static void 노선의_이름_상세_값_확인(String name) {
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();
        List<String> lineNames = response.jsonPath()
                .getList(".", LineAndSectionResponse.class)
                .stream()
                .map(LineAndSectionResponse::getLineName)
                .collect(toList());
        assertThat(lineNames).contains(name);
    }

    public static void 노선_응답_상태코드_검증(int statusCode, HttpStatus httpStatus) {
        assertThat(statusCode).isEqualTo(httpStatus.value());
    }

    private static ExtractableResponse<Response> callGetLinesByUri(String uri) {
        return RestAssured.given()
                .log()
                .all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(uri)
                .then()
                .log()
                .all()
                .extract();
    }

}
