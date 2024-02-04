package nextstep.subway.acceptance.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;
import nextstep.subway.section.SectionCreateRequest;

import static io.restassured.RestAssured.given;

public class SectionApiRequester {

    public static ExtractableResponse<Response> generateSection(
            SectionCreateRequest request,
            Long id
    ) {
        return given().log().all()
                .body(request)
                .pathParam("id", id)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/{id}/sections")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> deleteSection(Long lineId, Long stationId) {
        return given().log().all()
                .pathParam("id", lineId)
                .queryParam("stationId", stationId)
                .when().delete("/lines/{id}/sections")
                .then().log().all()
                .extract();
    }
}
