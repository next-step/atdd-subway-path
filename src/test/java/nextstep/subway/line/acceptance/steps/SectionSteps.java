package nextstep.subway.line.acceptance.steps;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionSteps {

    public static ExtractableResponse<Response> 지하철_노선에_지하철역_등록_요청( LineResponse lineResponse, SectionRequest sectionRequest) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(sectionRequest) // RequestBody
                .pathParam("id", lineResponse.getId()) // PathVariable
                .when()
                .post("/lines/{id}/sections")
                .then().log().all()
                .extract();
    }

    public static void 지하철_노선에_지하철역_등록_성공됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    public static void 등록된_지하철_노선에_역들이_순서대로_정렬됨(LineResponse lineResponse, List<Long> expectedIds) {
        ExtractableResponse<Response> srchResponse = 지하철_노선_정보_조회_요청됨(lineResponse);
        지하철_노선에_역들이_순서대로_정렬됨(srchResponse, expectedIds);
    }

    public static void 지하철_노선에_역들이_순서대로_정렬됨(ExtractableResponse<Response> response, List<Long> expectedIds) {
        assertThat(response.jsonPath()
                .getList("stations", StationResponse.class)
                .stream()
                .map(station -> station.getId())
                .collect(Collectors.toList()))
                .containsExactlyElementsOf(expectedIds);
    }

    public static void 지하철_노선_정보_조회됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static ExtractableResponse<Response> 지하철_노선_정보_조회_요청됨(LineResponse lineResponse) {
        return RestAssured.given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("id", lineResponse.getId()) // pathVariable
                .when()
                .get("lines/{id}")
                .then().log().all()
                .extract();
    }

    public static void 지하철_노선에_지하철역_제거_성공됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    public static void 지하철_노선에_지하철역_없음(LineResponse lineResponse, StationResponse stationResponse){
        assertThat(lineResponse.getStations()).doesNotContain(stationResponse);
    }

    public static ExtractableResponse<Response> 지하철_노선에_지하철역_제거_요청(LineResponse lineResponse, StationResponse stationResponse) {
        return RestAssured.given().log().all()
                .when()
                .delete("lines/{lineId}/staions?stationId={stationId]", lineResponse.getId(), stationResponse.getId())
                .then().log().all()
                .extract();
    }

    public static void 지하철_노선에_지하철역_등록됨(LineResponse lineResponse, SectionRequest sectionRequest) {
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(lineResponse, sectionRequest);
        지하철_노선에_지하철역_등록_성공됨(response);
    }
}
