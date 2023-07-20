package nextstep.subway.acceptance.steps;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static nextstep.subway.utils.CustomRestAssuredRequest.*;

public class StationSteps {

    public static ExtractableResponse<Response> 지하철역_생성_요청(String stationName) {
        Map<String, String> bodyParams = new HashMap<>();
        bodyParams.put("name", stationName);
        return requestPost("/stations", bodyParams);
    }

    public static ExtractableResponse<Response> 지하철역_목록_조회_요청() {
        return requestGet("/stations");
    }

    public static ExtractableResponse<Response> 지하철역_삭제_요청(Long stationId) {
        Map<String, Long> pathParams = new HashMap<>();
        pathParams.put("stationId", stationId);
        return requestDelete("/stations/{stationId}", pathParams);
    }

    public static List<String> 역_이름_목록_추출(ExtractableResponse<Response> response) {
        return response.jsonPath().getList("name", String.class);
    }

    public static Long 역_생성_ID_추출(ExtractableResponse<Response> response) {
        return response.jsonPath().getLong("id");
    }
}
