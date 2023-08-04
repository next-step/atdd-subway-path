package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.utils.ApiRequest;

public class PathSteps {
    public static ExtractableResponse<Response> 지하철_경로_조회_요청(Long source, Long target) {
        return ApiRequest.get("/paths?source=" + source + "&target=" + target);
    }
}
