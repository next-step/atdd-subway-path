package nextstep.subway.acceptance.steps;

import static nextstep.subway.utils.CustomRestAssuredRequest.requestDelete;
import static nextstep.subway.utils.CustomRestAssuredRequest.requestGet;
import static nextstep.subway.utils.CustomRestAssuredRequest.requestPost;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;

public class LineSteps {

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
}
