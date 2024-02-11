package nextstep.subway.common.api;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.core.RestAssuredHelper;

import java.util.Map;

public class PathApiHelper {

    public static final String PATH_API_PATH = "/paths";

    private PathApiHelper() {
    }

    public static ExtractableResponse<Response> findPath(final Long startStationId, final Long endStationId) {
        return RestAssuredHelper.get(PATH_API_PATH, Map.of("source", startStationId, "target", endStationId));
    }
}
