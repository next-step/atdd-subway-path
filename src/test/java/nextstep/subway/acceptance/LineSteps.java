package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.utils.ApiRequest;
import nextstep.subway.utils.ParameterFactory;

import java.util.Map;

public class LineSteps {
    public static ExtractableResponse<Response> 지하철_노선_생성_요청(String name, String color, Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = ParameterFactory.toLineCreateParams(name, color, upStationId, downStationId, distance);
        return ApiRequest.post("/lines", params);
    }

    public static ExtractableResponse<Response> 지하철_노선_생성_요청(Long upStationId, Long downStationId) {
        Map<String, String> params = ParameterFactory.toLineCreateParams(upStationId, downStationId);
        return ApiRequest.post("/lines", params);
    }

    public static ExtractableResponse<Response> 지하철_노선_수정_요청(String location, String color) {
        Map<String, String> params = ParameterFactory.toLineUpdateParams(color);
        return ApiRequest.put(location, params);
    }

    public static ExtractableResponse<Response> 지하철_노선_삭제_요청(String location) {
        return ApiRequest.delete(location);
    }

    public static ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return ApiRequest.get("/lines");
    }

    public static ExtractableResponse<Response> 지하철_노선_조회_요청(ExtractableResponse<Response> createResponse) {
        return ApiRequest.get(createResponse.header("location"));
    }

    public static ExtractableResponse<Response> 지하철_노선_조회_요청(Long id) {
        return ApiRequest.get("/lines/" + id);
    }

    public static ExtractableResponse<Response> 지하철_노선에_지하철_구간_생성_요청(Long lineId, Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = ParameterFactory.toSectionCreateParams(upStationId, downStationId, distance);
        return ApiRequest.post("/lines/" + lineId + "/sections", params);
    }

    public static ExtractableResponse<Response> 지하철_노선에_지하철_구간_제거_요청(Long lineId, Long stationId) {
        return ApiRequest.delete("/lines/" + lineId + "/sections?stationId=" + stationId);
    }
}
