package nextstep.subway.path.acceptance.step;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.path.domain.PathType;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.path.dto.PathStationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PathAcceptanceStep {
    public static ExtractableResponse<Response> 최단_거리_경로_조회_요청(Long sourceStationId, Long targetStationId) {
        return 경로_조회_요청(sourceStationId, targetStationId, PathType.DISTANCE);
    }

    public static ExtractableResponse<Response> 최소_시간_경로_조회_요청(Long sourceStationId, Long targetStationId) {
        return 경로_조회_요청(sourceStationId, targetStationId, PathType.DURATION);
    }

    private static ExtractableResponse<Response> 경로_조회_요청(Long sourceStationId, Long targetStationId, PathType type) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("source", sourceStationId);
        params.put("target", targetStationId);
        params.put("type", type);

        return RestAssured.given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .queryParams(params)
                .get("/paths")
                .then()
                .log().all()
                .extract();
    }

    public static void 최단_거리_경로_조회_응답됨(ExtractableResponse<Response> response) {
        경로_조회_응답됨(response);
    }

    public static void 최소_시간_경로_조회_응답됨(ExtractableResponse<Response> response) {
        경로_조회_응답됨(response);
    }

    private static void 경로_조회_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 최단_거리_경로_조회됨(ExtractableResponse<Response> response, List<Long> stationIds) {
        경로_조회됨(response, stationIds);
    }

    public static void 최소_시간_경로_조회됨(ExtractableResponse<Response> response, List<Long> stationIds) {
        경로_조회됨(response, stationIds);
    }

    private static void 경로_조회됨(ExtractableResponse<Response> response, List<Long> stationIds) {
        PathResponse pathResponse = response.as(PathResponse.class);
        assertThat(pathResponse.getStations()).extracting(PathStationResponse::getId).containsExactlyElementsOf(stationIds);
    }

    public static void 총_거리와_소요_시간을_함께_응답함(ExtractableResponse<Response> response) {
        PathResponse pathResponse = response.as(PathResponse.class);
        assertThat(pathResponse.getDistance()).isNotZero();
        assertThat(pathResponse.getDuration()).isNotZero();
    }
}
