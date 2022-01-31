package nextstep.subway.acceptance.step_feature;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.applicaion.dto.LineAndSectionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class LineStepFeature {

    public static final String 신분당선_이름 = "신분당선";
    public static final String 신분당선_색 = "red";
    public static final String 이호선_이름 = "2호선";
    private static final String CREATE_LINE_NAME_PARAM_KEY = "name";
    private static final String CREATE_LINE_COLOR_PARAM_KEY = "color";
    private static final String CREATE_LINE_UP_STATION_PARAM_KEY = "upStationId";
    private static final String CREATE_LINE_DOWN_STATION_PARAM_KEY = "downStationId";
    private static final String CREATE_LINE_DISTANCE_PARAM_KEY = "distance";
    private static final String LINE_BASE_URI = "lines";

    public static LineAndSectionResponse callCreateAndFind(Map<String, String> lineParams) {
        ExtractableResponse<Response> createResponse = callCreateLines(lineParams);
        String uri = createResponse.header("Location");

        ExtractableResponse<Response> response = callGetLinesByUri(uri);
        return response.as(LineAndSectionResponse.class);
    }

    public static ExtractableResponse<Response> callCreateLines(Map<String, String> lineParams) {
        return RestAssured.given()
                .log()
                .all()
                .body(lineParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(LINE_BASE_URI)
                .then()
                .log()
                .all()
                .extract();
    }

    public static ExtractableResponse<Response> callGetLines() {
        return RestAssured.given()
                .log()
                .all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(LINE_BASE_URI)
                .then()
                .log()
                .all()
                .extract();
    }

    public static ExtractableResponse<Response> callGetLines(String uri) {
        return RestAssured.given()
                .log()
                .all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(uri)
                .then()
                .log()
                .all()
                .extract();
    }

    public static ExtractableResponse<Response> callUpdateLines(Map<String, String> lineParams) {
        return RestAssured.given()
                .log()
                .all()
                .body(lineParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(LINE_BASE_URI + "/" + lineParams.get("id"))
                .then()
                .log()
                .all()
                .extract();
    }

    public static ExtractableResponse<Response> callDeleteLines(long id) {
        return RestAssured.given()
                .log()
                .all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete(LINE_BASE_URI + "/" + id)
                .then()
                .log()
                .all()
                .extract();
    }

    public static ExtractableResponse<Response> callAddSection(long lineId, long upStationId, long downStationId, int distance) {
        Map<Object, Object> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        return RestAssured.given()
                .log()
                .all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(LINE_BASE_URI + "/" + lineId + "/sections")
                .then()
                .log()
                .all()
                .extract();
    }

    public static ExtractableResponse<Response> callDeleteSection(long lineId, long stationId) {
        String deleteSectionUri = "/lines/%s/sections?stationId=%s";
        return RestAssured.given()
                .log()
                .all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete(String.format(deleteSectionUri, lineId, stationId))
                .then()
                .log()
                .all()
                .extract();
    }


    private static ExtractableResponse<Response> callGetLinesByUri(String uri) {
        return RestAssured.given()
                .log()
                .all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(uri)
                .then()
                .log()
                .all()
                .extract();
    }

    public static Map<String, String> createLineParams(String name, String color, Long upStationId,
                                                       Long downStationId, int distance) {
        Map<String, String> result = new HashMap();
        result.put(CREATE_LINE_NAME_PARAM_KEY, name);
        result.put(CREATE_LINE_COLOR_PARAM_KEY, color);
        result.put(CREATE_LINE_UP_STATION_PARAM_KEY, String.valueOf(upStationId));
        result.put(CREATE_LINE_DOWN_STATION_PARAM_KEY, String.valueOf(downStationId));
        result.put(CREATE_LINE_DISTANCE_PARAM_KEY, String.valueOf(distance));

        return result;
    }

    public static void checkCreateLine(ExtractableResponse<Response> response) {
        checkResponseStatus(response.statusCode(), HttpStatus.CREATED);
        assertThat(response.header("Location")).isNotBlank();
    }

    public static void checkCreateLineFail(ExtractableResponse<Response> response) {
        checkResponseStatus(response.statusCode(), HttpStatus.BAD_REQUEST);
    }

    public static void checkFindLine(ExtractableResponse<Response> response) {
        checkResponseStatus(response.statusCode(), HttpStatus.OK);
    }

    public static void checkResponseStatus(int statusCode, HttpStatus httpStatus) {
        assertThat(statusCode).isEqualTo(httpStatus.value());
    }

}
