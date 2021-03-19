package nextstep.subway.path.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class PathVerificationSteps {

    public static void 지하철_최단_경로_조회_됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 지하철_최단_경로_조회_실패_됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    public static void 지하철_역_조회_실패_됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    public static void 지하철_최단_경로_거리(ExtractableResponse<Response> response, int resultDistance) {
        PathResponse pathResponse = response.as(PathResponse.class);
        assertThat(pathResponse.getDistance()).isEqualTo(resultDistance);
    }

    public static void 지하철_최단_경로_포함_역(ExtractableResponse<Response> response, List<StationResponse> expectedStations) {
        List<String> expectedStationNames = expectedStations.stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());

        PathResponse pathResponse = response.as(PathResponse.class);
        List<Station> stations = pathResponse.getStations();

        List<String> resultStationNames = stations.stream()
                .map(station -> station.getName())
                .collect(Collectors.toList());

        assertThat(resultStationNames).containsAll(expectedStationNames);
    }
}
