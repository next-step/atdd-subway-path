package nextstep.subway.common;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.core.RestAssuredHelper;

import java.util.Map;

public class LineApiHelper {

    public static final String LINE_API_PATH = "/lines";

    private LineApiHelper() {
    }

    public static ExtractableResponse<Response> createLine(final String name, final String color, final Long upStationId, final Long downStationId, final int distance) {
        final Map<String, Object> createLineRequest = createLineRequestFixture(name, color, upStationId, downStationId, distance);
        return RestAssuredHelper.post(LINE_API_PATH, createLineRequest);
    }

    public static ExtractableResponse<Response> fetchLines() {
        return RestAssuredHelper.get(LINE_API_PATH);
    }

    public static ExtractableResponse<Response> fetchLineById(final Long id) {
        return RestAssuredHelper.getById(LINE_API_PATH, id);
    }

    public static ExtractableResponse<Response> removeLine(final Long stationId) {
        return RestAssuredHelper.deleteById(LINE_API_PATH, stationId);
    }

    public static ExtractableResponse<Response> modifyLine(final Long id, final String name, final String color) {
        return RestAssuredHelper.put(LINE_API_PATH, id, createLineUpdateRequestFixture(name, color));
    }

    private static Map<String, Object> createLineRequestFixture(final String name, final String color, final Long upStationId, final Long downStationId, final int distance) {
        return Map.of(
                "name", name
                , "color", color
                , "upStationId", upStationId
                , "downStationId", downStationId
                , "distance", distance
        );
    }

    private static Map<String, Object> createLineUpdateRequestFixture(final String name, final String color) {
        return Map.of(
                "name", name
                , "color", color
        );
    }

}
