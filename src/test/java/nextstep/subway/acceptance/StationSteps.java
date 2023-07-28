package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.utils.ApiRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StationSteps {
    public static ExtractableResponse<Response> 지하철역_생성_요청(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        return ApiRequest.post("/stations", params);
    }

    public static List<StationResponse> 지하철역_목록_조회_요청() {
        return ApiRequest.get("/stations").jsonPath()
                .getList(".", StationResponse.class);
    }

    public static List<String> 지하철역_이름_목록_조회_요청() {
        return ApiRequest.get("/stations").jsonPath()
                .getList("name", String.class);
    }

    public static ExtractableResponse<Response> 지하철역_삭제_요청(String location) {
        return ApiRequest.delete(location);
    }
}
