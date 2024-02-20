package nextstep.subway.acceptance.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.presentation.request.AddSectionRequest;
import nextstep.subway.line.presentation.response.AddSectionResponse;
import org.springframework.http.MediaType;

public class SectionAcceptanceStep {

    public static ExtractableResponse<Response> 지하철_구간_추가(AddSectionRequest addSectionRequest, Long lineId) {
        return RestAssured
                .given().log().all()
                .body(addSectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("lindId", lineId)
                .when().post("/lines/{lindId}/section")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_구간_삭제(Long lineId, Long stationId) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("lineId", lineId)
                .queryParam("stationId", stationId)
                .when().delete("/lines/{lineId}/sections")
                .then().log().all()
                .extract();
    }

    public static AddSectionResponse 지하철_구간_추가됨(Long upStation, Long downStation, int distance, Long lineId) {
        return 지하철_구간_추가(AddSectionRequest.of(upStation, downStation, distance), lineId).as(AddSectionResponse.class);
    }

}
