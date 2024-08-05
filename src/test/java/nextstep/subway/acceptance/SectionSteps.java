package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;
import nextstep.subway.presentation.SectionRequest;

public class SectionSteps {
    static ExtractableResponse<Response> 지하철_구간_생성(Long lineId, SectionRequest sectionRequest) {
        return RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();
    }

    static void 지하철_구간_삭제(Long lineId, Long stationId) {
        RestAssured.given().log().all()
                .when().delete("/lines/" + lineId + "/sections/" + stationId)
                .then().log().all();
    }
}
