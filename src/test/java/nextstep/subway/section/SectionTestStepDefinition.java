package nextstep.subway.section;


import static nextstep.subway.station.StationTestStepDefinition.지하철_역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class SectionTestStepDefinition {
    public static SectionResponse 지하철_구간_생성_요청(Long lineId, Long upStationId, Long downStationId, Integer distance) {
        SectionRequest sectionRequest = new SectionRequest(upStationId, downStationId, distance);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .pathParam("lineId", lineId)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(sectionRequest)
            .when().post("/lines/{lineId}/sections")
            .then().log().all()
            .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        return response.as(SectionResponse.class);
    }

    public static int 지하철_구간_생성_요청_상태_코드_반환(Long lineId, Long upStationId, Long downStationId, Integer distance) {
        SectionRequest sectionRequest = new SectionRequest(upStationId, downStationId, distance);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .pathParam("lineId", lineId)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(sectionRequest)
            .when().post("/lines/{lineId}/sections")
            .then().log().all()
            .extract();

        return response.statusCode();
    }

    public static int 지하철_구간_생성_요청_상태_코드_반환(Long lineId, String upStationName, String downStationName, Integer distance) {
        Long upStationId = 지하철_역_생성_요청(upStationName).getId();
        Long downStationId = 지하철_역_생성_요청(downStationName).getId();
        SectionRequest sectionRequest = new SectionRequest(upStationId, downStationId, distance);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .pathParam("lineId", lineId)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(sectionRequest)
            .when().post("/lines/{lineId}/sections")
            .then().log().all()
            .extract();

        return response.statusCode();
    }

    public static void 지하철_구간_제거_요청(Long lineId, Long stationId) {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .pathParam("lineId", lineId)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .params("stationId", stationId)
            .when().delete("/lines/{lineId}/sections")
            .then().log().all()
            .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    public static int 지하철_구간_제거_요청_상태_코드_반환(Long lineId, Long stationId) {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .pathParam("lineId", lineId)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .params("stationId", stationId)
            .when().delete("/lines/{lineId}/sections")
            .then().log().all()
            .extract();

        return response.statusCode();
    }

    public static PathResponse 지하철_경로_조회(Long sourceId, Long targetId) {
        ExtractableResponse<Response> response = 지하철_경로_조회_요청(sourceId, targetId);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        return response.as(PathResponse.class);
    }

    public static int 지하철_경로_조회_상태코드_반환(Long sourceId, Long targetId) {

        return 지하철_경로_조회_요청(sourceId, targetId).statusCode();
    }

    public static ExtractableResponse<Response> 지하철_경로_조회_요청(Long sourceId, Long targetId) {
        return RestAssured.given().log().all()
            .param("source", sourceId)
            .param("target", targetId)
            .when().get("paths")
            .then().log().all()
            .extract();
    }
}
