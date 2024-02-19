package nextstep.subway.acceptance.fixture;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.section.SectionRequest;
import org.springframework.http.MediaType;

import nextstep.subway.dto.line.LineRequest;

public class LineFixture {
    public static ExtractableResponse<Response> 노선_생성_요청(
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

    public static ExtractableResponse<Response> 노선_조회_요청(Long id) {
        return RestAssured
            .given()
            .pathParam("id", id)
            .when()
            .get("/lines/{id}")
            .then()
            .extract();
    }

    public static ExtractableResponse<Response> 노선_목록_조회_요청() {
        return RestAssured
            .when()
            .get("/lines")
            .then()
            .extract();
    }

    public static ExtractableResponse<Response> 구간_생성_요청(
        Long lineId, Long downStationId, Long upStationId, Integer distance
    ) {
        return RestAssured
            .given().log().all()
            .body(new SectionRequest(downStationId, upStationId, distance))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines/{id}/sections", lineId)
            .then().log().all()
            .extract();
    }
}
