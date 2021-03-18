package nextstep.subway.line.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class LineSteps {

    private static final String URI_LINES = "/lines";
    private static final String URI_SECTIONS = "/sections";
    private static final String URI_PATHS = "/paths";
    private static final String HEADER_LOCATION = "Location";

    public static ExtractableResponse<Response> 지하철_노선_등록되어_있음(Map<String, String> params) {
        return 지하철_노선_생성_요청(params);
    }

    public static ExtractableResponse<Response> 지하철_노선_등록되어_있음(String name,
                                                               String color,
                                                               StationResponse upStation,
                                                               StationResponse downStation,
                                                               int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", String.valueOf(upStation.getId()));
        params.put("downStationId", String.valueOf(downStation.getId()));
        params.put("distance", String.valueOf(distance));
        return 지하철_노선_생성_요청(params);
    }

    public static ExtractableResponse<Response> 지하철_노선_생성_요청(Map<String, String> params) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when()
                .post(URI_LINES)
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured.given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(URI_LINES)
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_조회_요청(LineResponse response) {
        return RestAssured.given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(URI_LINES + "/{lineId}", response.getId())
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_조회_요청(ExtractableResponse<Response> response) {
        String uri = response.header(HEADER_LOCATION);

        return RestAssured.given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(uri)
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_수정_요청(ExtractableResponse<Response> response, Map<String, String> params) {
        String uri = response.header(HEADER_LOCATION);

        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when()
                .put(uri)
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_제거_요청(ExtractableResponse<Response> response) {
        String uri = response.header(HEADER_LOCATION);

        return RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선에_지하철역_등록_요청(LineResponse line, StationResponse upStation, StationResponse downStation, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStation.getId() + "");
        params.put("downStationId", downStation.getId() + "");
        params.put("distance", distance + "");

        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when()
                .post(URI_LINES + "/{lineId}" + URI_SECTIONS, line.getId())
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선에_지하철역_제외_요청(LineResponse line, StationResponse station) {
        return RestAssured.given().log().all()
                .param("stationId", station.getId())
                .when()
                .delete(URI_LINES + "/{lineId}" + URI_SECTIONS, line.getId())
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 경로_조회_요청(StationResponse sourceStation, StationResponse targetStation) {
        return RestAssured.given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .param("source", sourceStation.getId())
                .param("target", targetStation.getId())
                .when()
                .get(URI_PATHS)
                .then()
                .log().all()
                .extract();
    }
}
