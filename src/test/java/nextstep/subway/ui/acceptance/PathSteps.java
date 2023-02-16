package nextstep.subway.ui.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.utils.Request;

import java.util.Map;

public class PathSteps {
    public static ExtractableResponse<Response> 경로_탐색_요청(Long source, Long target) {
        Map<String, String> params = Map.of(
                "source", source + "",
                "target", target + "");
        return Request.get("/paths", params);
    }
}
