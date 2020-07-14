package nextstep.subway.map.acceptance.step;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.map.dto.MapResponse;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class MapAcceptanceStep {

    public static ExtractableResponse<Response> 지하철_노선도_조회_요청() {
        return RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get("/maps")
                .then()
                .log().all()
                .extract();
    }

    public static void 지하철_노선도_조회_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 지하철_노선도에_노선별_지하철역_순서_정렬됨(ExtractableResponse<Response> response, Long lineId, Long... expectedStationIds) {
        final MapResponse mapResponse = response.as(MapResponse.class);
        final List<LineResponse> lineResponses = mapResponse.getLineResponses();
        assertThat(lineResponses).isNotNull();

        final Optional<LineResponse> maybeLine = lineResponses.stream()
                .filter(lineResponse -> lineId.equals(lineResponse.getId()))
                .findFirst();
        assertThat(maybeLine).isNotEmpty();

        final List<Long> stationIds = maybeLine.get().getStations().stream()
                .map(it -> it.getStation().getId())
                .collect(Collectors.toList());
        assertThat(stationIds).containsExactlyElementsOf(Arrays.asList(expectedStationIds));
    }
}
