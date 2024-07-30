package nextstep.subway.acceptance.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import io.restassured.RestAssured;
import org.springframework.http.MediaType;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.SectionResponse;

public class SectionRequestHelper {
    public static ResponseHelper<Void> requestDelete(Long lineId, Long stationId) {
        var response = RestAssured
                .given()
                .pathParam("id", lineId)
                .queryParam("stationId", stationId)
                .when()
                .delete("/lines/{id}/sections")
                .then()
                .extract();
        return new ResponseHelper<>(response, new TypeReference<>() {
        });
    }

    public static ResponseHelper<SectionResponse> requestCreate(Long lineId, SectionRequest request) {
        var contentType = MediaType.APPLICATION_JSON_VALUE;
        var path = "/lines/{lineId}/sections";

        var response = RestAssured.given().log().all()
                .body(request)
                .contentType(contentType)
                .pathParam("lineId", lineId)
                .when().post(path)
                .then().log().all()
                .extract();

        return new ResponseHelper<>(response, new TypeReference<>() {
        });
    }
}
