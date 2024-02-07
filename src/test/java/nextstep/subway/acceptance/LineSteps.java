package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class LineSteps {

    public static final String LINES_URL = "/lines";
    public static final String SLUSH = "/";

    private LineSteps() {
    }

    public static ExtractableResponse<Response> 노선이_생성되어_있다(final String name, final String color, final Long upStationId, final Long downStationId, final int distance) {
        Map<String, String> params = createLineRequestPixture(name, color, upStationId, downStationId, distance);
        return RestAssured
                .given().log().all().body(params).contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(LINES_URL)
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract();
    }

    public static ExtractableResponse<Response> 노선목록을_조회한다() {
        return RestAssured
                .given().log().all().contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get(LINES_URL)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }

    public static ExtractableResponse<Response> 노선을_조회한다(final Long lineId) {
        return RestAssured
                .given().log().all().contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get(LINES_URL + SLUSH + lineId)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 노선을_수정한다(final Long lineId, final String name, final String color) {
        final Map<String, String> modifyBody = modifyLineRequestPixture(lineId, name, color);
        return RestAssured
                .given().log().all().contentType(MediaType.APPLICATION_JSON_VALUE).body(modifyBody)
                .when().put(LINES_URL)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }

    public static void 노선을_삭제한다(final Long lineId) {
        RestAssured
                .given().log().all().contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete(LINES_URL + SLUSH + lineId)
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .extract();
    }

    public static Map<String, String> createLineRequestPixture(final String name, final String color,
                                                               final Long upStationId, final Long downStationId, final int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", String.valueOf(upStationId));
        params.put("downStationId", String.valueOf(downStationId));
        params.put("distance", String.valueOf(distance));
        return params;
    }

    private static Map<String, String> modifyLineRequestPixture(final Long id, final String name, final String color) {
        Map<String, String> params = new HashMap<>();
        params.put("id", String.valueOf(id));
        params.put("name", name);
        params.put("color", color);
        return params;
    }
}
