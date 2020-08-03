package nextstep.subway.line.acceptance.step;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class LineStationAcceptanceStep {
    public static void 지하철_노선에_지하철역_등록되어_있음(Long lineId, Long preStationId, Long stationId) {
        지하철_노선에_지하철역_등록_요청(lineId, preStationId, stationId);
    }

    public static void 지하철_노선에_지하철역_등록되어_있음(Long lineId, Long preStationId, Long stationId, int distance, int duration) {
        지하철_노선에_지하철역_등록_요청(lineId, preStationId, stationId, distance, duration);
    }

    public static ExtractableResponse<Response> 지하철_노선에_지하철역_등록_요청(Long lineId, Long preStationId, Long stationId) {
        Map<String, String> params = new HashMap<>();
        params.put("preStationId", preStationId + "");
        params.put("stationId", stationId + "");
        params.put("distance", "5");
        params.put("duration", "2");

        return RestAssured.given().log().all().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                body(params).
                when().
                post("/lines/{lineId}/stations", lineId).
                then().
                log().all().
                extract();
    }

    public static ExtractableResponse<Response> 지하철_노선에_지하철역_등록_요청(Long lineId, Long preStationId, Long stationId, int distance, int duration) {
        Map<String, String> params = new HashMap<>();
        params.put("preStationId", preStationId + "");
        params.put("stationId", stationId + "");
        params.put("distance", distance + "");
        params.put("duration", duration + "");

        return RestAssured.given().log().all().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                body(params).
                when().
                post("/lines/{lineId}/stations", lineId).
                then().
                log().all().
                extract();
    }

    public static ExtractableResponse<Response> 지하철_노선에_지하철역_제외_요청(Long lineId, Long stationId) {
        return RestAssured.given().log().all().
                when().
                delete("/lines/{lineId}/stations/{stationId}", lineId, stationId).
                then().
                log().all().
                extract();
    }

    public static void 지하철_노선에_지하철역_등록됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    public static void 지하철_노선_정보_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 지하철_노선에_지하철역_제외됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    public static void 지하철_노선에_지하철역_제외_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static void 지하철_노선에_지하철역_제외_확인됨(ExtractableResponse<Response> response, Long stationId) {
        List<Long> stationIds = response.as(LineResponse.class).getStations().stream()
                .map(it -> it.getStation().getId())
                .collect(Collectors.toList());

        assertThat(stationIds).doesNotContain(stationId);
    }

    public static void 지하철_노선에_지하철역_순서_정렬됨(ExtractableResponse<Response> response, List<Long> expectedStationIds) {
        LineResponse line = response.as(LineResponse.class);
        List<Long> stationIds = line.getStations().stream()
                .map(it -> it.getStation().getId())
                .collect(Collectors.toList());

        assertThat(stationIds).containsExactlyElementsOf(expectedStationIds);
    }
}
