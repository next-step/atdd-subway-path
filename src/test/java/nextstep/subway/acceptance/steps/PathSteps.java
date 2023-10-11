package nextstep.subway.acceptance.steps;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.utils.CustomRestAssuredRequest;

import java.util.List;

public class PathSteps {

    public static ExtractableResponse<Response> 지하철_경로_탐색_요청(Long sourceId, Long targetId) {
        return CustomRestAssuredRequest.requestGet("/paths?source=" + sourceId + "&target=" + targetId);
    }

    public static List<Long> 경로_탐색_역_ID_목록_추출(ExtractableResponse<Response> response) {
        return response.jsonPath().getList("stations.id", Long.class);
    }

    public static Long 경로_탐색_최단거리_추출(ExtractableResponse<Response> response) {
        return response.jsonPath().getLong("distance");
    }

}
