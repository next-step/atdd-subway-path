package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

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

    public static ExtractableResponse<Response> 지하철_노선_조회_요청(
        ExtractableResponse<Response> createResponse) {
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

    public static ExtractableResponse<Response> 지하철_노선_생성_요청(String name, String color,
        Long upStationId, Long downStationId, int distance) {

        Map<String, String> params = 지하철_노선_생성_요청_파라미터_생성(name, color, upStationId,
            downStationId, distance);

        return RestAssured
            .given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines")
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



    public static ExtractableResponse<Response> 지하철_노선에_지하철_구간_생성_요청(Long lineId, Long upStationId,
        Long downStationId, int distance) {

        Map<String, String> params = 지하철_구간_생성_요청_파라미터_생성(upStationId, downStationId,
            distance);

        return 지하철_노선에_지하철_구간_생성_요청(lineId, params);
    }

    public static ExtractableResponse<Response> 지하철_노선에_지하철_구간_생성_요청(Long lineId,
        Map<String, String> params) {
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

    private static Map<String, String> 지하철_노선_생성_요청_파라미터_생성(String name, String color,
        Long upStationId, Long downStationId, int distance) {
        Map<String, String> lineCreateParams;
        lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", name);
        lineCreateParams.put("color", color);
        lineCreateParams.put("upStationId", upStationId + "");
        lineCreateParams.put("downStationId", downStationId + "");
        lineCreateParams.put("distance", distance + "");
        return lineCreateParams;
    }

    private static Map<String, String> 지하철_구간_생성_요청_파라미터_생성(Long upStationId, Long downStationId,
        int distance) {

        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");

        return params;
    }
}
