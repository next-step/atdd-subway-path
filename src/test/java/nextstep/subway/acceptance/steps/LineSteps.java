package nextstep.subway.acceptance.steps;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static nextstep.subway.utils.CustomRestAssuredRequest.*;

public class LineSteps {

    public static ExtractableResponse<Response> 지하철_노선_생성_요청(String name, String color) {
        Map<String, Object> bodyParams = new HashMap<>();
        bodyParams.put("name", name);
        bodyParams.put("color", color);
        return requestPost("/lines", bodyParams);
    }

    public static ExtractableResponse<Response> 지하철_노선_생성_요청(String name, String color,
                                                             Long upStationId, Long downStationId, Long distance) {
        Map<String, Object> bodyParams = new HashMap<>();
        bodyParams.put("name", name);
        bodyParams.put("color", color);
        bodyParams.put("upStationId", upStationId);
        bodyParams.put("downStationId", downStationId);
        bodyParams.put("distance", distance);
        return requestPost("/lines", bodyParams);
    }

    public static ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return requestGet("/lines");
    }

    public static ExtractableResponse<Response> 지하철_노선_조회_요청(
            ExtractableResponse<Response> createResponse) {
        return requestGet(createResponse);
    }

    public static ExtractableResponse<Response> 지하철_노선_조회_요청(Long lineId) {
        Map<String, Long> pathParams = new HashMap<>();
        pathParams.put("lineId", lineId);
        return requestGet("/lines/{lineId}", pathParams);
    }

    public static ExtractableResponse<Response> 지하철_노선_수정_요청(Long lineId, String name, String color) {
        Map<String, Long> pathParams = new HashMap<>();
        pathParams.put("lineId", lineId);
        Map<String, String> bodyParams = new HashMap<>();
        bodyParams.put("name", name);
        bodyParams.put("color", color);
        return requestPut("/lines/{lineId}", pathParams, bodyParams);
    }

    public static ExtractableResponse<Response> 노선_삭제_요청(Long lineId) {
        Map<String, Long> pathParams = new HashMap<>();
        pathParams.put("lineId", lineId);
        return requestDelete("/lines/{lineId}", pathParams);
    }


    public static ExtractableResponse<Response> 지하철_노선에_지하철_구간_생성_요청(Long lineId, Long upStationId,
                                                                     Long downStationId, Long distance) {
        Map<String, Object> pathParams = new HashMap<>();
        pathParams.put("lineId", lineId);
        Map<String, Object> bodyParams = new HashMap<>();
        bodyParams.put("upStationId", upStationId);
        bodyParams.put("downStationId", downStationId);
        bodyParams.put("distance", distance);
        return requestPost("/lines/{lineId}/sections", pathParams, bodyParams);
    }

    public static ExtractableResponse<Response> 지하철_노선에_지하철_구간_제거_요청(Long lineId, Long stationId) {
        Map<String, Long> pathParams = new HashMap<>();
        pathParams.put("lineId", lineId);
        pathParams.put("stationId", stationId);
        return requestDelete("/lines/{lineId}/sections?stationId={stationId}", pathParams);
    }

    public static List<Long> 노선의_역ID_목록_추출(ExtractableResponse<Response> response) {
        return response.jsonPath().getList("stations.id", Long.class);
    }

    public static List<String> 노선_이름_목록_추출(ExtractableResponse<Response> response) {
        return response.jsonPath().getList("name", String.class);
    }

    public static String 노선_이름_추출(ExtractableResponse<Response> response) {
        return response.jsonPath().getString("name");
    }

    public static String 노선_색깔_추출(ExtractableResponse<Response> response) {
        return response.jsonPath().getString("color");
    }

    public static Long 노선_생성_ID_추출(ExtractableResponse<Response> response) {
        return response.jsonPath().getLong("id");
    }
}
