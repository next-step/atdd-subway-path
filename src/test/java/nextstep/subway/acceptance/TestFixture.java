package nextstep.subway.acceptance;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.application.dto.LineRequest;
import nextstep.subway.application.dto.SectionRequest;

public class TestFixture {

    public static ExtractableResponse<Response> createStation(String stationName) {
        Map<String, String> station = new HashMap<>();
        station.put("name", stationName);

        return RestAssured
            .given()
            .log().all()
            .body(station)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then()
            .log().all()
            .extract();
    }

    public static Long createStationAndGetId(String stationName) {
        ExtractableResponse<Response> response = createStation(stationName);
        return response.jsonPath().getLong("id");
    }

    public static ExtractableResponse<Response> getAllStations() {
        return RestAssured
            .given()
            .log().all()
            .when()
            .get("/stations")
            .then()
            .log().all()
            .extract();
    }

    public static ExtractableResponse<Response> deleteStation(Long stationId) {
        return RestAssured
            .given()
            .log().all()
            .when()
            .delete("/stations/" + stationId)
            .then()
            .log().all()
            .extract();
    }


    public static ExtractableResponse<Response> createLine(LineRequest request) {
        return RestAssured.given().log().all()
            .body(request)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines")
            .then().log().all()
            .extract();
    }

    public static Long createLineAndGetId(LineRequest request) {
        return createLine(request).jsonPath().getLong("id");
    }

    public static ExtractableResponse<Response> getLine(Long id) {
        return RestAssured.given().log().all()
            .when().get("/lines/" + id)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> getAllLines() {
        return RestAssured.given().log().all()
            .when().get("/lines")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> updateLine(Long id, LineRequest request) {
        return RestAssured.given().log().all()
            .body(request)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().put("/lines/" + id)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> deleteLine(Long id) {
        return RestAssured.given().log().all()
            .when().delete("/lines/" + id)
            .then().log().all()
            .extract();
    }


    public static ExtractableResponse<Response> addSection(Long lineId, Long upStationId, Long downStationId, int distance) {
        SectionRequest request = new SectionRequest(upStationId, downStationId, distance);
        return RestAssured
            .given()
            .log().all()
            .body(request)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines/" + lineId + "/sections")
            .then()
            .log().all()
            .extract();
    }

    public static ExtractableResponse<Response> removeSection(Long lineId, Long stationId) {
        return RestAssured
            .given()
            .log().all()
            .when()
            .delete("/lines/" + lineId + "/sections?stationId=" + stationId)
            .then()
            .log().all()
            .extract();
    }

    public static ExtractableResponse<Response> getPaths(Long source, Long target) {
        return RestAssured
            .given().log().all()
            .queryParam("source", source)
            .queryParam("target", target)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/paths")
            .then().log().all()
            .extract();
    }
}
