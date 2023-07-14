package nextstep.sections;

import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.section.model.SectionCreateRequest;

public class SectionFixture {

    public ExtractableResponse<Response> 구간생성(Long lineId, Long upstationId, Long downStationId, int distance) {
        SectionCreateRequest request = new SectionCreateRequest();
        request.setUpStationId(upstationId);
        request.setDownStationId(downStationId);
        request.setDistance(distance);

        return RestAssured.given().log().all()
                                                            .body(request).contentType(MediaType.APPLICATION_JSON_VALUE)
                                                            .when().post(getCreateSectionUrl(lineId))
                                                            .then().log().all()
                                                            .extract();
    }

    private String getCreateSectionUrl(Long lineId) {
        return "/lines/" + lineId + "/sections";
    }
}
