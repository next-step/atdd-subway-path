package nextstep.subway.map.acceptance.step;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.map.dto.MapResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;


public class MapAcceptanceStep {
    public static ExtractableResponse<Response> 지하철_노선도를_조회_요청() {
        return RestAssured.given().log().all().
                accept(MediaType.APPLICATION_JSON_VALUE).
                get("/maps").
                then().
                log().
                all().
                extract();
    }

    public static void 지하철_노선도_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 지하철_노선도에_노선별_지하철역_순서_정렬됨(ExtractableResponse<Response> response, Long lineId, List<Long> expectedStationIds) {
        MapResponse mapResponse = response.as(MapResponse.class);
        List<LineResponse> lines = mapResponse.getLineResponses();

        LineResponse line = lines.stream()
                .filter(it -> it.getId().equals(lineId))
                .findFirst()
                .orElseThrow(RuntimeException::new);

        List<Long> stationIds = line.getStations().stream()
                .map(it -> it.getStation().getId())
                .collect(Collectors.toList());

        assertThat(stationIds).containsExactlyElementsOf(expectedStationIds);
    }
}
