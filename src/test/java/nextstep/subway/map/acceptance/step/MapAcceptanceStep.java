package nextstep.subway.map.acceptance.step;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.map.dto.MapResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class MapAcceptanceStep {
    public static ExtractableResponse<Response> 지하철_노선도_캐시_조회_요청() {
         return RestAssured.given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/maps")
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선도_캐시_조회_요청(String eTag) {
        return RestAssured.given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .header("If-None-Match", eTag)
                .get("/maps")
                .then()
                .log().all()
                .extract();
    }

    public static void 지하철_노선도_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 지하철_노선도에_노선별_지하철역_순서_정렬됨(ExtractableResponse<Response> response, Long lineId, List<Long> stationIds) {
        List<LineResponse> lines = response.as(MapResponse.class).getLines();
        LineResponse line = lines.stream().filter(it -> Objects.equals(it.getId(), lineId)).findFirst().orElseThrow(RuntimeException::new);
        List<Long> lineStationsOfLine1 = line.getStations().stream().map(it -> it.getStation().getId()).collect(Collectors.toList());
        assertThat(lineStationsOfLine1).containsExactlyElementsOf(stationIds);
    }

    public static void 지하철_노선도_조회_시_ETag_포함됨(ExtractableResponse<Response> response) {
        assertThat(response.header("ETag")).isNotNull();
    }

    public static void 지하철_노선도_조회_캐시_적용됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_MODIFIED.value());
    }
}
