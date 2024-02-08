package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class SectionSteps {

    public static final String SECTIONS_URL = "/lines/%s/sections";

    private SectionSteps() {
    }

    public static ExtractableResponse<Response> 구간을_제거한다(final Long lineId, Long stationId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .queryParam("stationId", stationId)
                .when().delete(String.format(SECTIONS_URL, lineId))
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 구간을_등록한다(final Long lineId, final Long upStationId,
                                                   final Long downStationId, final int distance) {
        final Map<String, String> params = registerSectionRequestPixture(upStationId, downStationId, distance);
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(String.format(SECTIONS_URL, lineId))
                .then().log().all()
                .extract();
    }

    private static Map<String, String> registerSectionRequestPixture(final Long upStationId, final Long downStationId, final int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", String.valueOf(upStationId));
        params.put("downStationId", String.valueOf(downStationId));
        params.put("distance", String.valueOf(distance));
        return params;
    }
}
