package nextstep.subway.acceptance.step;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.dto.SectionTestRequest;
import org.springframework.http.MediaType;

public class SectionTestStep {

    public static ExtractableResponse<Response> 지하철역_구간_생성하기(SectionTestRequest request, Long lineId) {
        return RestAssured
                .given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철역_구간_삭제하기(Long lineId, Long stationId) {
        return RestAssured
                .given().log().all()
                .param("stationId", stationId)
                .when()
                .delete("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();
    }
}
