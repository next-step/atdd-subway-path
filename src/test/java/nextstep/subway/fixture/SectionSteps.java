package nextstep.subway.fixture;

import io.restassured.RestAssured;
import nextstep.subway.line.presentation.SectionRequest;
import nextstep.subway.line.presentation.SectionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class SectionSteps {


    public static SectionResponse 라인에_구간을_추가한다(long lineId, SectionRequest SectionRequest) {
        return RestAssured.given().log().all()
                .pathParam("lineId", lineId)
                .body(SectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .post("lines/{lineId}/sections")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(SectionResponse.class);
    }


    public static void 라인의_구간을_삭제한다(Long lineId, Long stationId) {
        RestAssured.given().log().all()
                .pathParam("lineId", lineId)
                .pathParam("stationId", stationId)
                .delete("lines/{lineId}/sections/{stationId}")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }
}
