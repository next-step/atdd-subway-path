package nextstep.subway.path.step;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class PathAcceptanceStep {

    public static ExtractableResponse<Response> 최단_거리_경로_조회_요청(Long sourceStationId, Long targetStationId) {
        return RestAssured.given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .queryParam("source", sourceStationId)
                .queryParam("target", targetStationId)
                .when()
                .get("/paths")
                .then()
                .log().all()
                .extract();
    }

    public static void 최단_거리_경로_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 최단_거리_경로의_지하철역_순서_정렬됨(ExtractableResponse<Response> response, List<Long> expectedStationIds) {
        PathResponse path = response.as(PathResponse.class);
        List<StationResponse> stations = path.getStations();

        List<Long> stationIds = stations.stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        assertThat(stationIds).containsExactlyElementsOf(expectedStationIds);
    }
}
