package nextstep.subway.acceptance.steps;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.fixture.LineFixture.*;

public class LineSteps {

    private static final String LINE_BASE_PATH = "/lines/";
    private static final String SECTION_BASE_PATH = "/lines/%s/sections";

    public static Long 지하철_노선이_생성되어_있음(final String name, final String color,
                                       final Long upStationId, final Long downStationId, final int distance) {
        return 지하철_노선_생성을_요청한다(name, color, upStationId, downStationId, distance).jsonPath().getLong("id");
    }

    public static ExtractableResponse<Response> 지하철_노선_생성을_요청한다(final String name, final String color,
                                                                final Long upStationId, final Long downStationId, final int distance) {
        final Map<String, String> params = 지하철_노선_생성_데이터를_만든다(name, color, upStationId, downStationId, distance);
        return RestAssured.given().log().all()
                .body(params)
                .accept(ContentType.ANY)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(LINE_BASE_PATH)
                .then().log().all()
                .extract();
    }

    private static Map<String, String> 지하철_노선_생성_데이터를_만든다(final String name, final String color,
                                                          final Long upStationId, final Long downStationId, final int distance) {
        final Map<String, String> params = new HashMap<>();
        params.put(LINE_NAME, name);
        params.put(LINE_COLOR, color);
        params.put(UP_STATION_ID, upStationId.toString());
        params.put(DOWN_STATION_ID, downStationId.toString());
        params.put(DISTANCE, String.valueOf(distance));
        return params;
    }

    public static ExtractableResponse<Response> 지하철_노선_목록_조회를_요청한다() {
        return RestAssured.given().log().all()
                .accept(ContentType.JSON)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(LINE_BASE_PATH)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_조회를_요청한다(final Long lineId) {
        return RestAssured.given().log().all()
                .accept(ContentType.JSON)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(LINE_BASE_PATH + lineId)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_변경을_요청한다(final Long lineId, final String updatedName, final String updatedColor) {
        final Map<String, String> params = 지하철_노선_변경_데이터를_만든다(updatedName, updatedColor);
        return RestAssured.given().log().all()
                .accept(ContentType.ANY)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when()
                .put(LINE_BASE_PATH + lineId)
                .then().log().all()
                .extract();
    }

    private static Map<String, String> 지하철_노선_변경_데이터를_만든다(final String updatedName, final String updatedColor) {
        final Map<String, String> params = new HashMap<>();
        params.put(LINE_NAME, updatedName);
        params.put(LINE_COLOR, updatedColor);
        return params;
    }

    public static ExtractableResponse<Response> 지하철_노선_삭제를_요청한다(final Long lineId) {
        return RestAssured.given().log().all()
                .accept(ContentType.ANY)
                .when()
                .delete(LINE_BASE_PATH + lineId)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_구간_등록을_요청한다(final Long lineId, final Long upStationId, final Long downStationId, final int distance) {
        final Map<String, String> params = 지하철_노선_구간_데이터를_만든다(upStationId, downStationId, distance);
        return RestAssured.given().log().all()
                .body(params)
                .accept(ContentType.ANY)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(String.format(SECTION_BASE_PATH, lineId))
                .then().log().all()
                .extract();
    }

    private static Map<String, String> 지하철_노선_구간_데이터를_만든다(final Long upStationId, final Long downStationId, final int distance) {
        final Map<String, String> params = new HashMap<>();
        params.put(UP_STATION_ID, upStationId.toString());
        params.put(DOWN_STATION_ID, downStationId.toString());
        params.put(DISTANCE, String.valueOf(distance));
        return params;
    }

    public static ExtractableResponse<Response> 지하철_노선_구간을_삭제_요청한다(final Long lineId, final Long stationId) {
        return RestAssured.given().log().all()
                .accept(ContentType.ANY)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .queryParam("stationId", stationId)
                .delete(String.format(SECTION_BASE_PATH, lineId))
                .then().log().all()
                .extract();
    }
}
