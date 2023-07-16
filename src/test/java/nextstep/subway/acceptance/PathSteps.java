package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.utils.AcceptanceTestUtils;

import java.util.Map;

public class PathSteps {

    public static ExtractableResponse<Response> 지하철_경로_조회(Long sourceId, Long targetId) {
        return AcceptanceTestUtils.get("/paths", Map.of("source", sourceId, "target", targetId));
    }
}
