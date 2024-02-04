package nextstep.subway.line.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.controller.dto.SectionAddRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;


public class SectionApiRequester {

    public static ExtractableResponse<Response> addSectionToLineSuccess(final long lineId, final SectionAddRequest body) {
        return RestAssured.given().log().all()
            .body(body)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .pathParam("lineId", lineId)
            .when().post("/lines/{lineId}/sections")
            .then().log().all()
            .statusCode(HttpStatus.CREATED.value())
            .extract();
    }

    public static ExtractableResponse<Response> addSectionToLine(final long lineId, final SectionAddRequest body) {
        return RestAssured.given().log().all()
            .body(body)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .pathParam("lineId", lineId)
            .when().post("/lines/{lineId}/sections")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> deleteSection(final long lineId, final long stationId) {
        return  RestAssured.given().log().all()
            .pathParam("lineId", lineId)
            .queryParam("stationId", stationId)
            .when().delete("/lines/{lineId}/sections")
            .then().log().all()
            .extract();
    }
}
