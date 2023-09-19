package nextstep.subway.acceptance.steps;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.utils.CustomRestAssuredRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PathSteps {

    public static ExtractableResponse<Response> 지하철_경로_탐색_요청(Long sourceId, Long targetId) {
        Map<String, Long> bodyParams = new HashMap<>();
        bodyParams.put("source", sourceId);
        bodyParams.put("target", targetId);
        return CustomRestAssuredRequest.requestGet("/paths", bodyParams);
    }

    public static List<Long> 경로_탐색_역_ID_목록_추출(ExtractableResponse<Response> response) {
        return response.jsonPath().getList("stations.id", Long.class);
    }

    public static Long 경로_탐색_최단거리_추출(ExtractableResponse<Response> response) {
        return response.jsonPath().getLong("distance");
    }

}
