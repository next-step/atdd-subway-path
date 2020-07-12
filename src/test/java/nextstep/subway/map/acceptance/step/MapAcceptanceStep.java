package nextstep.subway.map.acceptance.step;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineStationResponse;
import nextstep.subway.map.dto.MapResponse;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class MapAcceptanceStep {

    public static ExtractableResponse<Response> 지하철_노선도를_조회한다() {
        return RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get("/maps")
                .then()
                .log()
                .all()
                .extract();
    }

    public static void 지하철_노선도_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 지하철_노선도에_노선별_지하철역_순서_정렬됨(ExtractableResponse<Response> response, Long lineId, List<Long> expectedStationIds) {
        MapResponse mapResponse = response.as(MapResponse.class);
        List<LineResponse> lines = mapResponse.getLines();
        Optional<LineResponse> lineResponseOptional = lines.stream()
                .filter(lineResponse -> lineId.equals(lineResponse.getId()))
                .findFirst();

        assertThat(lineResponseOptional).isNotEmpty();

        List<LineStationResponse> stations = lineResponseOptional.get().getStations();
        assertThat(stations).extracting(LineStationResponse::getStation).extracting(StationResponse::getId)
                .containsExactlyElementsOf(expectedStationIds);
    }
}
