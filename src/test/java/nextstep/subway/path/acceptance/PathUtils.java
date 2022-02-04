package nextstep.subway.path.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.commons.AssertionsUtils.요청_실패;
import static nextstep.subway.commons.AssertionsUtils.조회요청_성공;
import static nextstep.subway.commons.RestAssuredUtils.get;
import static org.assertj.core.api.Assertions.assertThat;

public class PathUtils {

    private PathUtils() {
    }

    public static ExtractableResponse<Response> 지하철_최단경로_조회_요청(Long source, Long target) {
        return get("/paths?source=" + source + "&target=" + target);
    }

    public static void 지하철_최단경로_조회_성공(ExtractableResponse<Response> response) {
        조회요청_성공(response);
    }

    public static void 지하철_최단경로_조회_실패(ExtractableResponse<Response> response) {요청_실패(response);}

    public static void 지하철_최단경로_검증(ExtractableResponse<Response> response, Long... values) {
        List<StationResponse> stations = response.as(PathResponse.class).getStations();
        List<Long> stationIds = stations.stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
        assertThat(stationIds).containsExactly(values);
    }

}
