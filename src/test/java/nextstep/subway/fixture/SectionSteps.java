package nextstep.subway.fixture;

import io.restassured.RestAssured;
import nextstep.subway.line.SectionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Map;

public class SectionSteps {

    public static SectionResponse createSection(long lineId, Map<String, Object> body) {
        return RestAssured.given().log().all()
                .pathParam("lineId", lineId)
                .body(body)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .post("lines/{lineId}/sections")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(SectionResponse.class);
    }

    public static void deleteSection(long lineId, long stationId) {
        RestAssured.given().log().all()
                .pathParam("lineId", lineId)
                .pathParam("stationId", stationId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .delete("lines/{lineId}/sections/{stationId}")
                .then()
                .extract();
    }
}
