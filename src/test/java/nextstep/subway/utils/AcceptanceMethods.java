package nextstep.subway.utils;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.LineRequest;
import nextstep.subway.line.section.SectionRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Map;

public class AcceptanceMethods {
    public static ExtractableResponse<Response> makeStation(String stationName) {
        return RestAssured.given()
                .body(Map.of("name", stationName))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract();
    }

    public static ExtractableResponse<Response> makeLine(LineRequest lineRequest) {
        return RestAssured
                .given()
                .when()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .post("/lines")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract();
    }

    public static ExtractableResponse<Response> getLine(Long id) {
        ExtractableResponse<Response> response = RestAssured
                .given()
                .when()
                .get("/lines/" + id)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
        return response;
    }

    public static ExtractableResponse<Response> getLines() {
        return RestAssured
                .given()
                .when()
                .get("/lines")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }

    public static ExtractableResponse<Response> makeSection(Long lineId, SectionRequest sectionRequest) {
        return RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(sectionRequest)
                .when().log().all()
                .post("/lines/" + lineId + "/sections")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract();
    }

    public static void removeSection(Long stationId, Long lineId) {
        RestAssured
                .given()
                .param("stationId", stationId)
                .when()
                .delete("/lines/" + lineId + "/sections")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    public static ExtractableResponse<Response> getLineSections(Long lineId) {
        ExtractableResponse<Response> response = RestAssured
                .given()
                .when()
                .get("/lines/" + lineId + "/sections")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
        return response;
    }
}
