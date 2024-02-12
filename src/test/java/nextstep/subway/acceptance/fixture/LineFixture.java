package nextstep.subway.acceptance.fixture;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import nextstep.subway.dto.line.LineRequest;

public class LineFixture {
    public static ExtractableResponse<Response> createLine(
        String name, String color, Integer distance, Long upStationId, Long downStationId
    ) {
        return RestAssured
            .given()
            .body(new LineRequest(name, color, distance, upStationId, downStationId))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then()
            .extract();
    }

    public static ExtractableResponse<Response> getLine(Long id) {
        return RestAssured
            .given()
            .pathParam("id", id)
            .when()
            .get("/lines/{id}")
            .then()
            .extract();
    }

    public static ExtractableResponse<Response> getLines() {
        return RestAssured
            .when()
            .get("/lines")
            .then()
            .extract();
    }
}
