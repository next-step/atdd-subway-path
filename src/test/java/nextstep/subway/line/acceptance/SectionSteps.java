package nextstep.subway.line.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.SectionRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionSteps {

    public static ExtractableResponse<Response> 지하철_노선_구간_등록되어_있음(SectionRequest sectionRequest, long lineId) {
        return 지하철_노선_구간_등록_요청(sectionRequest, lineId);
    }

    public static ExtractableResponse<Response> 지하철_노선_구간_등록_요청(SectionRequest sectionRequest, long lineId) {
        return RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .pathParam("lineId", lineId)
                .post("/lines/{lineId}/sections")
                .then()
                .log().all().extract();
    }

    public static void 지하철_노선_구간_응답_확인(int statusCode, HttpStatus status) {
        assertThat(statusCode).isEqualTo(status.value());
    }

    public static ExtractableResponse<Response> 지하철_구간_제거_요청(long lineId, long stationId) {
        return RestAssured
                .given().log().all()
                .pathParam("lineId", lineId)
                .queryParam("stationId", stationId)
                .when().delete("/lines/{lineId}/sections")
                .then().log().all()
                .extract();
    }
}
