package nextstep.subway.fixture;

import io.restassured.RestAssured;
import nextstep.subway.line.Section;
import nextstep.subway.line.SectionRequest;
import nextstep.subway.line.SectionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Map;

public class SectionSteps {


    public static SectionResponse createSection(long lineId, SectionRequest SectionRequest) {
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
}
