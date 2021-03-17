package nextstep.subway.path.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class PathSteps {
    public static ExtractableResponse<Response> 최단거리_조회_요청(Long sourceId, Long targetId) {
        return RestAssured.given().log().all()
                .when()
                .params("sourceId", sourceId, "targetId", targetId)
                .get("/paths")
                .then().log().all().extract();
    }

    public static void 최단경로에_지하철역_순서_정렬됨(ExtractableResponse<Response> response, List<StationResponse> expectedStations) {
        List<StationResponse> stations = response.body().as(PathResponse.class).getStations();

        List<Long> stationIds = stations.stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());

        List<Long> expectedStationIds = expectedStations.stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());

        assertThat(stationIds).containsExactlyElementsOf(expectedStationIds);
    }
}
