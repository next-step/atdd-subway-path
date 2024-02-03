package nextstep.subway.common;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.core.RestAssuredHelper;

import java.util.Map;

public class SectionApiHelper {

    public static final String SECTION_API_PATH = "/lines/{id}/sections";

    private SectionApiHelper() {
    }

    public static ExtractableResponse<Response> createSection(final Long lineId, final Long upStationId, final Long downStationId, final int distance) {
        final Map<String, Object> createLineRequest = createRequestFixture(upStationId, downStationId, distance);
        return RestAssuredHelper.post(SECTION_API_PATH, lineId, createLineRequest);
    }

    public static ExtractableResponse<Response> removeSection(final Long lineId, final Long stationId) {
        return RestAssuredHelper.deleteByIdWithParam(SECTION_API_PATH, lineId, Map.of("stationId", stationId));
    }

    private static Map<String, Object> createRequestFixture(final Long upStationId, final Long downStationId, final int distance) {
        return Map.of(
                "upStationId", upStationId
                , "downStationId", downStationId
                , "distance", distance
        );
    }

}
