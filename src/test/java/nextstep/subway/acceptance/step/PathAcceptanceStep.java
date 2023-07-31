package nextstep.subway.acceptance.step;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.constants.Endpoint;
import nextstep.subway.support.RestAssuredClient;

import java.util.HashMap;
import java.util.Map;

public class PathAcceptanceStep {

    private static final String PATH_BASE_URL = Endpoint.PATH_BASE_URL.getUrl();

    /**
     * <pre>
     * 최단 거리 경로를 조회하는 API를 호출하는 함수
     * </pre>
     *
     * @param source 출발역 ID
     * @param target 도착역 ID
     * @return ExtractableResponse
     */
    public static ExtractableResponse<Response> 최단_거리_경로_조회를_요청한다(Long source, Long target) {
        Map<String, ?> queryParamMap = new HashMap<>(){{
           put("source", source);
           put("target", target);
        }};
        return RestAssuredClient.get(PATH_BASE_URL, queryParamMap);
    }


}
