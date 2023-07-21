package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.section.dto.CreateSectionRequest;
import org.springframework.http.MediaType;

public class SectionSteps {

    public static CreateSectionRequest 구간등록요청_생성(Long newUpStationId, Long newDownStationId, int distance) {
        return new CreateSectionRequest(newUpStationId, newDownStationId, distance);
    }

    public static ExtractableResponse<Response> 구간등록요청(CreateSectionRequest request) {
        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/{lineId}/sections", 1L)
                .then().log().all()
                .extract();
    }
}
